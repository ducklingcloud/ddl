/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package net.duckling.ddl.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public final class FileUtil {
	
	public static final String POINT = ".";
	
	/**
	 * Brief Intro Here
	 * @param file 需要写的文件
	 * @param content 需要写的文件内容
	 * @param charset 指定字符编码
	 * @return 写文件是否成功
	 */
	public static boolean writeFile(File file, String content, String charset) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), charset));
			bw.write(content);
			bw.flush();
			bw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Brief Intro Here
	 * 复制文件
	 * @param
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	/**
	 * Brief Intro Here
	 * 复制文件夹
	 * @param
	 */
	public static void copyDirectory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir)
						.getAbsolutePath()
						+ File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectory(dir1, dir2);
			}
		}
	}
	
	  /** Size of the buffer used when copying large chunks of data. */
    private static final int      BUFFER_SIZE = 4096;
    private static final Logger   LOG         = Logger.getLogger(FileUtil.class);

    /**
     *  Private constructor prevents instantiation.
     */
    private FileUtil()
    {}

    /**
     *  Makes a new temporary file and writes content into it. The temporary
     *  file is created using <code>File.createTempFile()</code>, and the usual
     *  semantics apply.  The files are not deleted automatically in exit.
     *
     *  @param content Initial content of the temporary file.
     *  @param encoding Encoding to use.
     *  @return The handle to the new temporary file
     *  @throws IOException If the content creation failed.
     *  @see java.io.File#createTempFile(String,String,File)
     */
    public static File newTmpFile( String content, String encoding )
        throws IOException
    {
        Writer out = null;
        Reader in  = null;
        File   f   = null;

        try
        {
            f = File.createTempFile( "jspwiki", null );

            in = new StringReader( content );

            out = new OutputStreamWriter( new FileOutputStream( f ),
                                          encoding );

            copyContents( in, out );
        }
        finally
        {
            if( in  != null ) {
            	in.close();
            }
            if( out != null ) {
            	out.close();
            }
        }

        return f;
    }

    /**
     *  Creates a new temporary file using the default encoding
     *  of ISO-8859-1 (Latin1).
     *
     *  @param content The content to put into the file.
     *  @throws IOException If writing was unsuccessful.
     *  @return A handle to the newly created file.
     *  @see #newTmpFile( String, String )
     *  @see java.io.File#createTempFile(String,String,File)
     */
    public static File newTmpFile( String content )
        throws IOException
    {
        return newTmpFile( content, "ISO-8859-1" );
    }

    /**
     *  Runs a simple command in given directory.
     *  The environment is inherited from the parent process (e.g. the
     *  one in which this Java VM runs).
     *
     *  @return Standard output from the command.
     *  @param  command The command to run
     *  @param  directory The working directory to run the command in
     *  @throws IOException If the command failed
     *  @throws InterruptedException If the command was halted
     */
    public static String runSimpleCommand( String command, String directory )
        throws IOException,
               InterruptedException
    {
        StringBuffer result = new StringBuffer();

        LOG.info("Running simple command "+command+" in "+directory);

        Process process = Runtime.getRuntime().exec( command, null, new File(directory) );

        BufferedReader stdout = null;
        BufferedReader stderr = null;

        try
        {
            stdout = new BufferedReader( new InputStreamReader(process.getInputStream()) );
            stderr = new BufferedReader( new InputStreamReader(process.getErrorStream()) );

            String line;

            while( (line = stdout.readLine()) != null )
            {
                result.append( line).append("\n");
            }

            StringBuffer error = new StringBuffer();
            while( (line = stderr.readLine()) != null )
            {
                error.append( line).append("\n");
            }

            if( error.length() > 0 )
            {
                LOG.error("Command failed, error stream is: "+error);
            }

            process.waitFor();

        }
        finally
        {
            // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
            process.getInputStream().close();
            if( stdout != null ){
            	stdout.close();
            }
            if( stderr != null ){
            	stderr.close();
            }
        }

        return result.toString();
    }


    /**
     *  Just copies all characters from <I>in</I> to <I>out</I>.  The copying
     *  is performed using a buffer of bytes.
     *
     *  @since 1.5.8
     *  @param in The reader to copy from
     *  @param out The reader to copy to
     *  @throws IOException If reading or writing failed.
     */
    public static void copyContents( Reader in, Writer out )
        throws IOException
    {
        char[] buf = new char[BUFFER_SIZE];
        int bytesRead = 0;

        while ((bytesRead = in.read(buf)) > 0)
        {
            out.write(buf, 0, bytesRead);
        }

        out.flush();
    }

    /**
     *  Just copies all bytes from <I>in</I> to <I>out</I>.  The copying is
     *  performed using a buffer of bytes.
     *
     *  @since 1.9.31
     *  @param in The inputstream to copy from
     *  @param out The outputstream to copy to
     *  @throws IOException In case reading or writing fails.
     */
    public static void copyContents( InputStream in, OutputStream out )
        throws IOException
    {
        byte[] buf = new byte[BUFFER_SIZE];
        int bytesRead = 0;

        while ((bytesRead = in.read(buf)) > 0)
        {
            out.write(buf, 0, bytesRead);
        }

        out.flush();
    }

    /**
     *  Reads in file contents.
     *  <P>
     *  This method is smart and falls back to ISO-8859-1 if the input stream does not
     *  seem to be in the specified encoding.
     *
     *  @param input The InputStream to read from.
     *  @param encoding The encoding to assume at first.
     *  @return A String, interpreted in the "encoding", or, if it fails, in Latin1.
     *  @throws IOException If the stream cannot be read or the stream cannot be
     *          decoded (even) in Latin1
     */
    public static String readContents( InputStream input, String encoding )
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileUtil.copyContents( input, out );

        ByteBuffer     bbuf        = ByteBuffer.wrap( out.toByteArray() );

        Charset        cset        = Charset.forName( encoding );
        CharsetDecoder csetdecoder = cset.newDecoder();

        csetdecoder.onMalformedInput( CodingErrorAction.REPORT );
        csetdecoder.onUnmappableCharacter( CodingErrorAction.REPORT );

        try
        {
            CharBuffer cbuf = csetdecoder.decode( bbuf );

            return cbuf.toString();
        }
        catch( CharacterCodingException e )
        {
            Charset        latin1    = Charset.forName("ISO-8859-1");
            CharsetDecoder l1decoder = latin1.newDecoder();

            l1decoder.onMalformedInput( CodingErrorAction.REPORT );
            l1decoder.onUnmappableCharacter( CodingErrorAction.REPORT );

            try
            {
                bbuf = ByteBuffer.wrap( out.toByteArray() );

                CharBuffer cbuf = l1decoder.decode( bbuf );

                return cbuf.toString();
            }
            catch( CharacterCodingException ex )
            {
                throw (CharacterCodingException) ex.fillInStackTrace();
            }
        }
    }

    /**
     *  Returns the full contents of the Reader as a String.
     *
     *  @since 1.5.8
     *  @param in The reader from which the contents shall be read.
     *  @return String read from the Reader
     *  @throws IOException If reading fails.
     */
    public static String readContents( Reader in )
        throws IOException
    {
        Writer out = null;

        try
        {
            out = new StringWriter();

            copyContents( in, out );

            return out.toString();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch( Exception e )
            {
                LOG.error("Not able to close the stream while reading contents.");
            }
        }
    }

    /**
     *  Returns the class and method name (+a line number) in which the
     *  Throwable was thrown.
     *
     *  @param t A Throwable to analyze.
     *  @return A human-readable string stating the class and method.  Do not rely
     *          the format to be anything fixed.
     */
    public static String getThrowingMethod( Throwable t )
    {
        StackTraceElement[] trace = t.getStackTrace();
        StringBuffer sb = new StringBuffer();

        if( trace == null || trace.length == 0 )
        {
            sb.append( "[Stack trace not available]" );
        }
        else
        {
            sb.append( trace[0].isNativeMethod() ? "native method" : "" );
            sb.append( trace[0].getClassName() );
            sb.append(".");
            sb.append(trace[0].getMethodName()+"(), line "+trace[0].getLineNumber());
        }
        return sb.toString();
    }
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }

    }  
    public static boolean deleteFolders(String strFolder)
    {
        boolean flag = false;
        File folder = new File(strFolder);
        if(!folder.isDirectory())
        {
            if(LOG.isDebugEnabled())
            {
                LOG.debug("the folder: "+strFolder+" is not a correct folder");
            }
            return flag;
        }
        flag = deleteFilesInFolder(strFolder);
        folder.delete(); 
        return flag;
    }
    public static boolean deleteFilesInFolder(String strFolder)
    {
        boolean flag = false;
        File folder = new File(strFolder);
         if (!folder.exists()) {
            return flag;
        }
        String[] files = folder.list();
        for(int i=0;i<files.length;i++)
        {
            String fileName = strFolder + File.separator +files[i];
            File file = new File(fileName);
            if(file.isDirectory())
            {
                flag = deleteFolders(fileName);
            }
            else
            {
                file.delete();
            }
        }
        return flag;
    }
    
    public static String saveAsFile(InputStream in, String path) {
		try {
			File file=new File(path);
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.copy(in, fos);
			in.close();
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException("save tmp file error:"+path,e);
		}
		return path;
	}
    
    
    /** 
     * 删除单个文件 
     * @param path 被删除文件的文件名 
     * @return 删除成功true，否则false 
     */  
    public static boolean deleteFile(String path) {  
        File file = new File(path);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            return true;
        }  
        return false;
    }  
    
    /**
     * 获取文件扩展名
	 * @param fileName
	 * @return
	 */
    public static String getFileExt(String fileName) {
    	int index = fileName.lastIndexOf('.');
		if(index<0){
			return null;
		}
		return fileName.substring(index+1).toLowerCase();
	}
}

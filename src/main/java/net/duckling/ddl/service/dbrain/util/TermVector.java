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
package net.duckling.ddl.service.dbrain.util;

/**
 * 此类用于计算向量的余弦相似度
 * @author owen shen
 *
 */
    public class TermVector
    {
        public static double ComputeCosineSimilarity(float[] vector1, float[] vector2)
        {
		if (vector1.length != vector2.length)
			try
			{
				throw new Exception("DIFER LENGTH");
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


            double denom = (VectorLength(vector1) * VectorLength(vector2));
            if (denom == 0D)
            {
            	return 0D;
            }
            else
            {    
            	return (InnerProduct(vector1, vector2) / denom);
            }

        }

        public static double InnerProduct(float[] vector1, float[] vector2)
        {

            if (vector1.length != vector2.length)
				try 
            	{
					throw new Exception("DIFFER LENGTH ARE NOT ALLOWED");
				} 
            	catch (Exception e) 
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


            double result = 0D;
            for (int i = 0; i < vector1.length; i++)
            {   
            	result += vector1[i] * vector2[i];
            }

            return result;
        }

        public static double VectorLength(float[] vector)
        {
        	double sum = 0D;
            for (int i = 0; i < vector.length; i++)
            {
            	sum = sum + (vector[i] * vector[i]);
            }
            return (double)Math.sqrt(sum);
        }

    }


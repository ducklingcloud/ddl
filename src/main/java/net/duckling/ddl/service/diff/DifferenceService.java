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

package net.duckling.ddl.service.diff;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.Resource;

/**
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface DifferenceService{
    /**
     *   Returns valid XHTML string to be used in any way you please.
     *
     *   @param context The VWB Context
     *   @param firstWikiText The old text
     *   @param secondWikiText the new text
     *   @return XHTML, or empty string, if no difference detected.
     */
    String makeDiff(VWBContext context, Resource resource, int verFirst, int verSecond);
    
    String getDiffResult(VWBContext context,String oldHtml,String newHtml);
}

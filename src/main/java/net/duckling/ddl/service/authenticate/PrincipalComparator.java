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

package net.duckling.ddl.service.authenticate;

import java.io.Serializable;
import java.security.Principal;
import java.text.Collator;
import java.util.Comparator;

/**
 * Comparator class for sorting objects of type Principal. Used for sorting
 * arrays or collections of Principals.
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public class PrincipalComparator implements Comparator, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Compares two Principal objects.
     * 
     * @param o1
     *            the first Principal
     * @param o2
     *            the second Principal
     * @return the result of the comparison
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare(Object o1, Object o2) {
        Collator collator = Collator.getInstance();
        if (o1 instanceof Principal && o2 instanceof Principal) {
            return collator.compare(((Principal) o1).getName(), ((Principal) o2).getName());
        }
        throw new ClassCastException("Objects must be of type Principal.");
    }

}

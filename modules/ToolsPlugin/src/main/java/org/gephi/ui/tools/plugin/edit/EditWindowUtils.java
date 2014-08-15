/*
 Copyright 2008-2011 Gephi
 Authors : Eduardo Ramos
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.ui.tools.plugin.edit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.gephi.attribute.time.TimestampBooleanSet;
import org.gephi.attribute.time.TimestampByteSet;
import org.gephi.attribute.time.TimestampCharSet;
import org.gephi.attribute.time.TimestampDoubleSet;
import org.gephi.attribute.time.TimestampFloatSet;
import org.gephi.attribute.time.TimestampIntegerSet;
import org.gephi.attribute.time.TimestampLongSet;
import org.gephi.attribute.time.TimestampSet;
import org.gephi.attribute.time.TimestampShortSet;
import org.gephi.attribute.time.TimestampStringSet;

/**
 *
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
public class EditWindowUtils {

    /**
     * These AttributeTypes are not supported by default by netbeans property editor. We will use attributes of these types as Strings and parse them.
     */
    public static final Set<Class> NOT_SUPPORTED_TYPES;
    
    static {
        final Set<Class> notSupportedTypes = new HashSet<Class>();
        
        //Prinitives Array
        notSupportedTypes.add(Boolean[].class);
        notSupportedTypes.add(boolean[].class);
        notSupportedTypes.add(Integer[].class);
        notSupportedTypes.add(int[].class);
        notSupportedTypes.add(Short[].class);
        notSupportedTypes.add(short[].class);
        notSupportedTypes.add(Long[].class);
        notSupportedTypes.add(long[].class);
        notSupportedTypes.add(BigInteger[].class);
        notSupportedTypes.add(Byte[].class);
        notSupportedTypes.add(byte[].class);
        notSupportedTypes.add(Float[].class);
        notSupportedTypes.add(float[].class);
        notSupportedTypes.add(Double[].class);
        notSupportedTypes.add(double[].class);
        notSupportedTypes.add(BigDecimal[].class);
        notSupportedTypes.add(Character[].class);
        notSupportedTypes.add(char[].class);
        
        //Dynamic
        notSupportedTypes.add(TimestampSet.class);
        notSupportedTypes.add(TimestampBooleanSet.class);
        notSupportedTypes.add(TimestampIntegerSet.class);
        notSupportedTypes.add(TimestampShortSet.class);
        notSupportedTypes.add(TimestampLongSet.class);
        notSupportedTypes.add(TimestampByteSet.class);
        notSupportedTypes.add(TimestampFloatSet.class);
        notSupportedTypes.add(TimestampDoubleSet.class);
        notSupportedTypes.add(TimestampCharSet.class);
        notSupportedTypes.add(TimestampStringSet.class);

        //Assign
        NOT_SUPPORTED_TYPES = Collections.unmodifiableSet(notSupportedTypes);
    }
     
    interface AttributeValueWrapper {

        public Byte getValueByte();

        public void setValueByte(Byte object);

        public Short getValueShort();

        public void setValueShort(Short object);

        public Character getValueCharacter();

        public void setValueCharacter(Character object);

        public String getValueString();

        public void setValueString(String object);

        public Double getValueDouble();

        public void setValueDouble(Double object);

        public Float getValueFloat();

        public void setValueFloat(Float object);

        public Integer getValueInteger();

        public void setValueInteger(Integer object);

        public Boolean getValueBoolean();

        public void setValueBoolean(Boolean object);

        public Long getValueLong();

        public void setValueLong(Long object);

        /**
         * **** Other types are not supported by property editors by default so they are used and parsed as Strings *****
         */
        public String getValueAsString();

        public void setValueAsString(String value);
    }
}

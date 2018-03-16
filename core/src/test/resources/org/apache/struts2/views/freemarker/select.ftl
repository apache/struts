<#--
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<@s.select theme="simple" list=['a','b','c'] />
<@s.select theme="simple" list=1 />
<@s.select theme="simple" list={'key':'value'} />
<@s.select theme="simple">
<@s.optgroup label="label1" list={'optgroupKey1':'optgroupValue1','optgroupKey2':'optgroupValue2'} />
<@s.optgroup label="label2" disabled=true list=['optgroupKey3','optgroupKey4'] />
<@s.optgroup label="label3" disabled=true list=2 />
</@s.select>
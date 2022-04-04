//--------------------------------------------------------------------------
//Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are
//met:
//
//Redistributions of source code must retain the above copyright notice,
//this list of conditions and the following disclaimer.
//Redistributions in binary form must reproduce the above copyright
//notice, this list of conditions and the following disclaimer in the
//documentation and/or other materials provided with the distribution.
//Neither the name of the Drew Davidson nor the names of its contributors
//may be used to endorse or promote products derived from this software
//without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
//COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
//OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
//AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
//THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
//DAMAGE.
//--------------------------------------------------------------------------
package com.opensymphony.xwork2.conversion;

import java.util.Map;

/**
 * <p>
 * Interface for handling null results from Chains.
 * Object has the opportunity to substitute an object for the
 * null and continue.
 * </p>
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public interface NullHandler
{
    /**
     *  Method called on target returned null.
     *  @param context context
     *  @param target target object
     *  @param methodName method name
     *  @param args arguments
     *
     *  @return object
     */
    Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args);
    
    /**
     *   Property in target evaluated to null.  Property can be a constant
     *   String property name or a DynamicSubscript.
     *
     *  @param context context
     *  @param target target object
     *  @param property property
     *
     *  @return object
     */
    Object nullPropertyValue(Map<String, Object> context, Object target, Object property);
}
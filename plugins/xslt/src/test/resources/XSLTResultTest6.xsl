<?xml version="1.0" encoding="UTF-8"?>
<!--
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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" />
    <xsl:template match="result">
        <books>
            <xsl:for-each select="books/item">
                <book>
                    <title><xsl:value-of select="title"/></title>
                    <author><xsl:value-of select="author"/></author>
                    <editions>
                    <xsl:for-each select="editions/item">
                        <edition><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></edition>
                    </xsl:for-each>
                    </editions>
                </book>
            </xsl:for-each>
        </books>
    </xsl:template>
</xsl:stylesheet>

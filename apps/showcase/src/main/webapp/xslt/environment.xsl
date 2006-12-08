<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:template match="/result">
        <html>
            <head>
                <title>JVM Info</title>
            </head>
            <body>
                <h1>JVM Info</h1>
                <xsl:value-of select="info/classpath"/>
                <table>
                    <tr><th>System Property</th><th>Value</th></tr>
                    <xsl:for-each select="info/systemProperties/entry">
                        <tr>
                            <td><xsl:value-of select="key"/></td>
                            <td><xsl:value-of select="value"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>

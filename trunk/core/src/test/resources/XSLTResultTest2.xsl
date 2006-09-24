<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict">

    <xsl:template match="result">
        <html>
            <body>
                Hello <xsl:value-of select="username"/> how are you?
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
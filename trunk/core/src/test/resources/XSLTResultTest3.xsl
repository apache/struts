<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict">

    <xsl:template match="result">
        <html>
            <body>
                Hello <xsl:value-of select="username"/> how are you?
                <p/>
                We have the following books:
                <xsl:for-each select="books/item">
                    <br/><xsl:value-of select="title"/> by <xsl:value-of select="author"/>.
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>

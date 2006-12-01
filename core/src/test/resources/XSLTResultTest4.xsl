<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict">

    <xsl:template match="result">
        <xsl:copy-of select="document('validators.xml')"/>
    </xsl:template>

</xsl:stylesheet>
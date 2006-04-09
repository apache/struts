<%@ taglib prefix="ww" uri="/webwork" %>
<%@ taglib prefix="cewolf" uri="/cewolf" %>

<H2>Sample charts</H2>

<ww:set name="lineDataSet" value="lineChartProducer" scope="request"/>
<ww:set name="pieDataSet" value="pieChartProducer" scope="request"/>

<cewolf:chart 
    id="line" 
    title="Sample line chart" 
    type="line" 
    xaxislabel="X" 
    yaxislabel="Y">
    <cewolf:data>
        <cewolf:producer id="lineDataSet"/>
    </cewolf:data>
</cewolf:chart>

<cewolf:chart 
    id="pie" 
    title="Sample pie chart" 
    type="pie">
    <cewolf:data>
        <cewolf:producer id="pieDataSet"/>
    </cewolf:data>
</cewolf:chart>
<p/>
<cewolf:img chartid="line" renderer="/cewolf" width="400" height="300"/> <cewolf:img chartid="pie" renderer="/cewolf" width="400" height="300"/>

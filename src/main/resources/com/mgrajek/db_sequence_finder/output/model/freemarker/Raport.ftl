<html>
<head>
<meta charset="utf-8"/>
<style>
.htmltut_table, .htmltut_table td
{
border: 1px solid;
}
.genome_distance, .genome_distance td
{
border-right: 1px dotted;
}
</style>

</head>
<body>
<h1>Sequences matches</h1>

<#list rangeMatches as range>
  <h2>RANGE: ${range.rangeName}</h2>
  <hr/>
  <#list range.matchedSequences as matchedSequence>
    <h3>${matchedSequence.startIndex}..${matchedSequence.endIndex}:</h3>

    <table class="htmltut_table">
      <tr>
      <#list matchedSequence.nucleotydes as nucleotyde>
        <td><center><b>${nucleotyde.name}</b></center></td>
      </#list>
      </tr>
      <tr>
      <#list matchedSequence.nucleotydes as nucleotyde>
        <td>${nucleotyde.value}</td>
      </#list>
      </tr>
    </table>

    <#if matchedSequence.wholeGeneMatchedPresent>
      <br/>
      <div><b>whole</b></div>
      <#list matchedSequence.wholeGenesMatch as gene>
        <div>${gene.name} <#if gene.complement>COMPLEMENT</#if></div>
      </#list>
    </#if>
    <#if matchedSequence.partialGeneMatchedPresent>
      <br/>
      <div><b>partial</b></div>
      <#list matchedSequence.partialGenesMatch as gene>
        <div>${gene.name} <#if gene.complement>COMPLEMENT</#if></div>
      </#list>
    </#if>
    <#if matchedSequence.rightNearestGeneMatchedPresent>
      <br/>
      <div><b>nearest:</b></div>
      <table >
      <#list matchedSequence.rightNearestGene as nGene>
        <tr>
          <td class="genome_distance">${nGene.gene.name} <#if nGene.gene.complement>COMPLEMENT</#if></td><td>${nGene.distance}</td>
        </tr>
      </#list>
      </table>
    </#if>
    <#if matchedSequence.leftNearestGeneMatchedPresent>
      <br/>
      <div><b>nearest COMPLEMENT:</b></div>
      <table >
      <#list matchedSequence.leftNearestGene as nGene>
        <tr>
          <td class="genome_distance">${nGene.gene.name} <#if nGene.gene.complement>COMPLEMENT</#if></td><td>${nGene.distance}</td>
        </tr>
      </#list>
      </table>
    </#if>
  </#list>

</#list>

</body>
</html>
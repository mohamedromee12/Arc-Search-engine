var outputHTML = "";
var animals = ["cat" , "dog" , "goat"];
$.getJSON('data.json',function(data){
    //display your data however you want.    
    console.log(data);
    animals = data;
})
outputHTML += "<table>"
for(var i=0; i<animals.length; i++)
{
 
  outputHTML += "<tr>";
  outputHTML += "<td > " + animals[i]  +"</td>";
  outputHTML += "</tr>"; 

  
}  
outputHTML += "</table>"
document.getElementById("output_div").innerHTML = outputHTML;


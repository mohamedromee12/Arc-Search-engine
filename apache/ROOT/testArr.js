const data = [ 1 , 2 , 3] ;
var count =0;
const readLine = require('readline');
const fs = require('fs');
var file = './arr.txt';
var rl = readLine.createInterface({
    input : fs.createReadStream(file),
    output : process.stdout,
    terminal: false
});
rl.on('line', function (text) {
 console.log(text);
 data[count] = text;
  //console.log(data[count]);
  count++;
});
fs.writeFileSync('data.json', JSON.stringify(data));
// var outputHTML = "";


// outputHTML += "<table>"
// for(var i=0; i<animals.length; i++)
// {
 
//   outputHTML += "<tr>";
//   outputHTML += "<td > " + animals[i]  +"</td>";
//   outputHTML += "</tr>"; 

  
// }  
// outputHTML += "</table>"
// document.getElementById("output_div").innerHTML = outputHTML;
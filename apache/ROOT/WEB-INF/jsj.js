let numPages = document.getElementById('buttons').childElementCount;
let mainDiv = document.getElementById('mainDiv');


let arrB = [];
for (let i=0; i<numPages; i++)
{
    arrB[i] = document.getElementById(i.toString());
   
}

for (let i=0; i<numPages; i++)
{
    let divSelected = document.getElementById((i+numPages).toString())
    if (divSelected.id != numPages.toString())
          divSelected.className += " displayNone";  
      
}

let arrD = [];
for (let i=0; i<numPages; i++)
{
    arrB[i].addEventListener ('click', function () {
        let divSelected = document.getElementById((i+numPages).toString())

        
        divSelected.className = "midDiv displayNow"
      
        for (let j=0; j<numPages; j++)
        {
            arrD[j] = document.getElementById((j+numPages).toString());
            if (arrD[j].id != (i+numPages).toString())
            {
                
                arrD[j].className = "midDiv displayNone";
                
            }
        }
        
    })
    // if (i == 0)
    // {
    //     arrB[i].className += "DefultB OrangeB curveLeft";
    // }
    // if (i == numPages-1)
    // {
    //     arrB[i].className += "DefultB BlueB curveRight";
    // }
    // if (i == numPages/2)
    // {
    //     arrB[i].className += "DefultB GradiantB";
    // }
    // else if (i<numPages/2 && i!= 0)
    // {
    //     arrB[i].className += "DefultB OrangeB";
    // }
    // else
    // {
    //     if (i!= numPages-1)
    //         arrB[i].className += "DefultB BlueB";
    // }

}
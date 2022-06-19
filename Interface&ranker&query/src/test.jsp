<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
     
        <title>ARC</title>
      
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="stylesheet" href="CSS/allmin.css">
       
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,600;0,700;0,800;1,300;1,400;1,600;1,700;1,800&display=swap" rel="stylesheet">
        <link rel="preconnect" href="https://fonts.googleapis.com">
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
      <link href="https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap" rel="stylesheet">
    </head>
<body style="background-image: url(./images/pexels-faik-akmd-1025469.jpg);  background-repeat: no-repeat;
background-size:100% 100%; height: 100vh;">
 <div class="mt-4" id="div_language" style="display: none;" >
    <h2 class="mb-3 text-light">Select Language</h2>
    <select class="form-select bg-secondary text-light" id="select_language" onchange="updateCountry()"></select>
    <select class="form-select bg-secondary text-light mt-2" id="select_dialect"></select>
  </div>
    <form action="NameGenderRequest" method="GET" id="NameGenderRequest" style="display: flex; 
    flex-direction:row;
    align-items: center;
    margin: 20px;">
       <img src="images/ARC.png" class="" style="width: 100px;" alt="">
          
        <br>
        <div id="searchBox" style="width: 500px;
        border-radius: 55px;
        padding: 25px;
        background: #e1e9f2;
        display: flex;
        align-items: center;
        box-shadow: 6px 6px 10px -1px rgb(0 0 0 / 15%), 0px 0px 10px 2px rgb(97 153 199);
        max-width: 80%;
        height: 10px;
        position: relative;">
       
        
          <img src="images/searchengin-brands.svg" alt="" id="googleIcon" style="width: 30px;
          cursor: pointer;">
          <input type="text" placeholder="Search ARC or type a URL" id="final" name="Name" class="" style="border: 0;
          background: transparent;
          outline: none;
          transition: 2s;
          max-width: 100%;
          width: 80%;
          height: 50px;
          margin-left: 10px;">
         
          <button class="btn btn-success "  type="button" id="start" style="width: 36px   ;
          transition: 0.5;
          transition-delay: 0.5s;
          background-color: transparent !important;
          border-color: transparent !important;
          cursor: pointer;
          position: absolute;
          right: 20px;"><img src="images/microphone-lines-solid.svg" alt="" class=""></button>
         
        </div>
        <br>
       

        <input type="submit" value="Search" class="" style="padding: 8px 10px;
        background-color: #e9964d;
        color: white;
        border-radius: 13px;
        border-color: transparent;
        cursor: pointer;
        transition: 0.5s !important;
        width: fit-content !important;
        position: relative;
        margin-left: 10px;" id="stop">
  </form>
  <div class="mt-4" style="display: none;">
    <button class="btn btn-success" id="start"><img src="images/microphone-lines-solid.svg" alt="" class="mic-icon"></button>
    <button class="btn btn-danger" id="stop">Stop</button>
    <p id="status" class="lead mt-3 text-light" style="display: none" style="background-image: url(images/pexels-faik-akmd-1025469.jpg);">Listenting ...</p>
  </div>
   <div >
       <h1 style="color: red;">
           message
       </h1>
   </div> 



   <script src="./js.js"></script>
   <script src="./language.js"></script>
   <script src="./speech.js"></script>
</body>
</html>
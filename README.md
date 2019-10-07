# Capstone
<h1>First Iteration</h1> <br>
Car World Application for Android<br>
Running firebase database and cloudstorage<br><br><br>

CONTAINS SO FAR: <br>
<h3>REGISTER</h3> (USER ABLE TO REGISTER AND SAVE THEIR INFORMATION): Information is saved in Firebase authentication , real time database along with storage for images for which url is saved in the database (Image implementation in beta)<br><br>
<h3>REGISTRATION:</h3> User is able to register using their email. Once done so they will be taking to setup page where they can enter their username, fullname and car type. Once done so user will be taken to newsfeed page. If user has registered but not completed setup then if they sign in again they will be taken to setup.
<h3>SIGN IN:</h3> Sign in is based with Firebase authentication checking if user has registered which is also using firebase email authentication. If user has Registered but not saved their profile information then they will be taken to setup page where they will be granted for their information. Clicking on profile will save profile in database. (Displaying of image in beta in this iteration) <br>
<br><br>
<h3>ONCE SIGNED IN AND REGISTERED:</h3>Taken to news feed. Got a navigation slider set up (bottom navigation will be added)

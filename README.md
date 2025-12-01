# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

---------------------------------------------------------------------------------------

## <mark>IMPORTANT: Setting Up Firebase Admin SDK<mark>

In order for the register/login flows to work when running the program locally on an IDE (such as IntelliJ), 
we require an additional file containing the Firebase service account key relating to the Firebase Project we set up online.
**Importantly, this service account key corresponds to the Google account you use to join our Firebase Project, so whoever**
**wants to run our app will likely need their own unique copy. We kindly invite interested parties to provide us**
**with a gmail address they don't mind sharing so that we may add them to our Firebase Project**
**<mark>(point of contact: kenshin.newkfonheytow@mail.utoronto.ca)<mark>**

Once you are added to the Firebase Project, follow these steps to obtain and configure your Firebase service account key:

1. Go to the **Firebase Console**  
   https://console.firebase.google.com/

2. Select your project: **uoft-eats-bd368**

3. Click the **⚙️ (Settings icon)** next to *Project Overview*  
   → Choose **Project settings**

4. Navigate to the **Service accounts** tab

5. Under **Firebase Admin SDK**, make sure the language is set to **Java**

6. Click **Generate new private key**

7. Confirm by clicking **Generate key** in the popup

8. A JSON file will download automatically  
   Rename this file to:  
   **`service-account-key.json`**

9. Move the JSON file to your project’s **root directory** (same level as .idea, src)

---------------------------------------------------------------------------------------

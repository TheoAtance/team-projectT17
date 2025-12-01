# Team Project
## Summary
Welcome to UofT-Eats, a restaurant review guide catered to University of Toronto Students. Users can create accounts 
through Google, browse different restaurants around campus, quickly filter for what they are feeling, review other 
restaurants, and create a personal list of favourites. Our program also offers accessibility options such as translation 
for students less familiar with English so they can still enjoy the app.
## DeepL API setup

Our translation feature uses the [DeepL API](https://www.deepl.com/pro-api).  
To enable it, you need a DeepL API key and must expose it as an environment variable called `DEEPL_API_KEY`.

### 1. Obtain a DeepL API key

1. Go to the DeepL website: https://www.deepl.com/pro
2. Create an account (or log in if you already have one).
3. Choose "Get started for free", there will be a free api plan.
4. After signing up, open your **Account** / **API** section.
5. Copy your **Authentication Key** (“API key” or “auth key”).  
   It will look like a long string of letters and numbers.

### 2. Set the `DEEPL_API_KEY` environment variable

Set the environment variable so the application can read it at runtime.

#### On Windows (PowerShell)

```powershell
# Set for the current terminal session
$env:DEEPL_API_KEY = "your-deepl-auth-key-here"
```

#### On MacOS / Linux (Terminal)

1. Open your terminal.
2. Add this line to `~/.bashrc` or `~/.zshrc`:

   ```bash
   export DEEPL_API_KEY="your-deepl-auth-key-here"
## User Stories
### Kenshin
As a user, I want to register an account using a username and password or a Google account, so that my 
personal restaurant data is saved securely.

As a user, I want to be able to log in and log out using my Google account or a username and password, so 
that I can log in on different devices and my account remains secure.
### Theo
As a user, I want to browse all the restaurants within a given radius around University of Toronto, so 
that I can find restaurants close to campus.
### Prabeer
As a user, I want to filter restaurants organized in a list based on categories I'm in the mood for that I 
can find what I want quickly.
### Raymond
As a user, I want to be able to add a review to a restaurant so that I can share my opinion on the best 
and worst restaurants around campus.
### Dean
As a user, I want to save my favorite restaurants into a list, so I can access them later.
### Justin
As a user I want to translate any given review into a language I'm more familiar with, so I can interact with the 
diverse student body at UofT.
### Zihao Wang
As a user I hope the app can recommend restaurants based on my preferences.
## APIs
UofT-Eats uses the [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview) for 
data on the restaurants and the [DeepL API](https://www.deepl.com/pro-api) for help translating the reviews and [Openai API](https://openai.com/api/) for recommendations.
## Screenshots
### Log In
![Log In](src/main/resources/images/log_in_screen.png)
### Main Page
![Main Page](src/main/resources/images/main_screen.png)
### Filter
![Filter](src/main/resources/images/filter_screen.png)
### Restaurant
![Filter](src/main/resources/images/restaurant_screen.png)

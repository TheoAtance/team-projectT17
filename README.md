# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.

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

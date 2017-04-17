MailHelper
==========
**Mail organisation and address creation.**
---------------------------------------

Information
-----------

This uses WW2 Enigma to create email addresses relevant to the service you're creating. The enigma code was provided by [CollinJ/Enigma](https://github.com/CollinJ/Enigma), and edited to work with this project.

If you're using this, ensure to change the ``enigmaSettings`` in ``config.json``, as this is the encryption key.

###**Just a point to note!**
Enigma is not a secure encryption, if you don't know this it's really wort a read. Mr. Alan Turing, and his team at Bletchley Park (UK), cracked Enigma which contributed massivley to the German's loosing the war. 

From reading up on it several times, the fault in this encryption seems to be that a letter can never be itself. The Bombe Machine made to crack enigma exploited this and was able to crack enigma in a few minutes.

Usage
-----
Simply auto run the MailHelper.exe or MailHelper.jar on login.

This was made for a wildcard setup, but now my private version creates individual addresses to prevent spam. A small SQL/web hook can do this for you, contact me if you require assistance.

Compile
-------
``mvn clean package`` it's as simple as that really..
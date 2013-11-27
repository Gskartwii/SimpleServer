setPort(80);
setThreaded(false); --Whether or not the server runs in a separate thread from main application. Generally this will be false, as you'll most likely create your own configuration class if you want to run this with your own application.
setDocumentRoot("www/"); --The forward slash at the end is needed for cross-OS compatability.
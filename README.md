# voice-assistant-backend
backend program of voice-assistant
采用了阿里云的实时语音识别sdk，和科大讯飞的分词sdk  
前后端用websocket通讯，前端浏览器录音实时传到后端，进行实时语音识别，识别完一句话后进行分词和词性分析  
浏览器录音的模块需要安全模式wss，启动浏览器时要进行设置才能启动录音  
这是后端代码，前端采用vue库，代码见voice-assistant-frontend


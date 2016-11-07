# devfest2016_sample

발표자료: http://www.slideshare.net/kingori/ss-68326596

## 소켓서버 설정 방법

1.    [socket io master zip](https://github.com/socketio/socket.io/archive/master.zip) 다운로드
2.    서버가 ack 를 처리하도록 examples/chat/index.js 수정 

    ```
    socket.on('new message', function (data, ack) {
      // we tell the client to execute 'new message'
      socket.broadcast.emit('new message', {
        username: socket.username,
        message: data
      });
      if( ack) {
        ack('ack');
      }
    });
    ```  
3.    [chat server 실행](https://github.com/socketio/socket.io/tree/master/examples/chat)   

    ```
    $ cd socket.io
    $ npm install
    $ cd examples/chat
    $ npm install
    $ DEBUG=socket.io* node .
    ```

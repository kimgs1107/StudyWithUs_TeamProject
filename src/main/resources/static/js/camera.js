const URL = "/my_model/";

let model, webcam, webcamContainer, maxPredictions;
webcamContainer = document.querySelector("#webcam-container")

let url = window.location.href;
let urlList = url.split("/");
let curTID = urlList[urlList.length-1];
console.log(curTID);

let hour=0,minute=0,second=0,active=false,timeoutId,totalTime,realTime;
// Load the image model and setup the webcam

async function init() {
    document.querySelector("#start").setAttribute("hidden","true");
    document.querySelector("#stop").removeAttribute("hidden");
    // document.querySelector("#restart").removeAttribute("hidden");

    let webSocket = new WebSocket("ws://"+window.location.host+"/exist");
    webSocket.onopen = function(event){
        webSocket.send(curTID); //팀 정보를 전송
    }
    webSocket.onmessage = function(event){
        var message = event.data.split(" ");
        let box = document.getElementById("box"+message[1]);
        let id = document.getElementById(message[1]);

        if(message[0] == "leave"){
            members.removeChild(box);
        }
        else {
            if (box == null) {
                let box = document.createElement("div");
                box.setAttribute("id", "box"+message[1]);
                box.setAttribute("width", "100px")
                box.setAttribute("style", "display:inline-block; margin-top:5px");

                let userBox = document.createElement("div");

                let userName = document.createElement("div");
                userName.innerHTML = message[3];
                userName.setAttribute("style", "text-align:center;");

                let userImg = document.createElement("img");
                if(message[2] == "noImage"){
                    userImg.setAttribute("src", "/adminImage/userIcon.png");
                }else {
                    userImg.setAttribute("src", message[2]);
                }
                userImg.setAttribute("style", "width:50px; height:50px; border-radius:50%; margin: 5px 10px;");

                let id = document.createElement("div");
                id.innerHTML = message[0];
                if(message[0] === "ON"){
                    id.setAttribute("style", "color:red; font-size:16pt; text-align:center;");
                }
                else{
                    id.setAttribute("style", "color:black; font-size:16pt; text-align:center;");
                }
                id.setAttribute("id", message[1]);

                userBox.append(userName);
                userBox.append(userImg);
                box.append(userBox);
                box.append(id);

                members.append(box);
            } else {
                id.innerHTML = message[0];
                if(message[0] === "ON"){
                    id.setAttribute("style", "color:red; font-size:16pt; text-align:center;");
                }
                else{
                    id.setAttribute("style", "color:black; font-size:16pt; text-align:center;");
                }
            }
        }

        console.log("In onmessage");
        console.log(event.data);
    }

    $.ajax({
        type: "POST",
        url: "/getTotalTime",
        contentType: 'application/json',
        data: JSON.stringify({tID: curTID}),
        success: function (data) {
            console.log("total" + data)
            totalTime+=0
            totalTime=data*100;
        },
        error: function (error) {
            console.log(error);
        }
    }); // 총시간 셋팅 (스터디 생성 이후 시간++)
    $.ajax({
        type: "POST",
        url: "/getRealTime",
        contentType: 'application/json',
        data: JSON.stringify({tID: curTID}),
        success: function (data) {
            console.log("realTime" + data)
            realTime+=0
            realTime=data*100;
        },
        error: function (error) {
            console.log(error);
        }
    }); // 하루공부시간 셋팅 (이전시간 ++)

    var members = document.querySelector("#members");
    $.ajax({
        type: "POST",
        url: "/members",
        contentType: 'application/json',
        data: JSON.stringify({tID: curTID}),
        success: function (data) {
            data.forEach(function(user){
                let box = document.createElement("div");
                box.setAttribute("id", "box"+user.uuID);
                box.setAttribute("width", "100px")
                box.setAttribute("style", "display:inline-block; margin-top:5px");

                let userBox = document.createElement("div");

                let userName = document.createElement("div");
                userName.innerHTML = user.userName;
                userName.setAttribute("style", "text-align:center;");

                let userImg = document.createElement("img");
                if(user.userImage == null){
                    userImg.setAttribute("src", "/adminImage/userIcon.png");
                }else {
                    userImg.setAttribute("src", user.userImage);
                }
                userImg.setAttribute("style", "width:50px; height:50px; border-radius:50%; margin: 5px 10px;");

                let id = document.createElement("div");
                id.innerHTML = user.exist;
                if(user.exist === "ON"){
                    id.setAttribute("style", "color:red; font-size:16pt; text-align:center;");
                }
                else{
                    id.setAttribute("style", "color:black; font-size:16pt; text-align:center;");
                }
                id.setAttribute("id", user.uuID);

                userBox.append(userName);
                userBox.append(userImg);
                box.append(userBox);
                box.append(id);

                members.append(box);
            });
        },
        error: function (error) {
            console.log(error);
        }
    });

    const modelURL = URL + "model.json";
    const metadataURL = URL + "metadata.json";

    model = await tmImage.load(modelURL, metadataURL);
    maxPredictions = model.getTotalClasses();
    flip = true; // whether to flip the webcam . 반전처리
    webcam = new tmImage.Webcam(200,200,flip); // width, height, flip

    await webcam.setup(); // request access to the webcam
    await webcam.play();

    document.querySelector("#img").setAttribute("hidden","true");
    webcamContainer.replaceChild(webcam.canvas,document.querySelector("canvas"));

    window.requestAnimationFrame(loop);
}

var camOFF = false;
async function loop() {
    if(camOFF) { // 캠 정지하면 exist=false, reat/totalTime 셋팅
        clearInterval(timeoutId);
        window.cancelAnimationFrame(loop);
        $.ajax({
            type: "POST",
            url: "/updateUserTeam",
            contentType: 'application/json',
            data: JSON.stringify({data: false, realTime:realTime, totalTime:totalTime, tID: curTID}),
            success: function (data) {     },
            error: function (error) {
                console.log(error);
            }
        });
        document.querySelector('canvas').setAttribute("hidden","true");
        document.querySelector('img').removeAttribute("hidden");
        return;
    }
    webcam.update(); // update the webcam frame
    await predict();// 주석하면 겁나 빨라
    window.requestAnimationFrame(loop);
}

let cnt=0;
let data;
async function predict() { //프레임한번
    const prediction = await model.predict(webcam.canvas);
    for (let i = 0; i < maxPredictions; i++) { // 학습한 모델의 갯수만큼 돈다.

        if(prediction[1].probability.toFixed(2) < 0.2){ // 자리이탈
            cnt++;
            if(cnt<=500){ // 자리이탈 5초까지는 있는 시간으로 측정해서 ++
                realTime++;
                totalTime++
            }
        }

        if(cnt>500){ // 100 = 1초  // 30000=300초=5분 // 테스트는 5초로. 5초 넘어가면 exist=false, real/totalTime 저장
            clearInterval(timeoutId);
            active = false;
            data=false;
            $.ajax({
                type: "POST",
                url: "/updateUserTeam",
                contentType: 'application/json',
                data: JSON.stringify({data: data, realTime:realTime, totalTime:totalTime, tID: curTID}),
                success: function (data) {
                },
                error: function (error) {
                    console.log(error);
                }
            });
            document.querySelector('canvas').setAttribute("hidden","true");
            document.querySelector('img').removeAttribute("hidden");
        }

        if(prediction[1].probability.toFixed(2) > 0.8){ // 자리있음
            realTime++;
            totalTime++;
            cnt=0;
            data=true;


            $.ajax({
                type:"POST",
                url:"/checkExist",
                contentType: 'application/json',
                data:JSON.stringify({tID: curTID}),
                success: function(data){
                    if(data==false){
                        $.ajax({
                            type: "POST",
                            url: "/updateUserTeam",
                            contentType: 'application/json',
                            data: JSON.stringify({data: true, realTime:realTime, totalTime:totalTime, tID: curTID}),
                            success: function (data) {
                            },
                            error: function (error) {
                                console.log(error);
                            }
                        });
                    }
                }
            })


            document.querySelector('img').setAttribute("hidden","true");
            document.querySelector('canvas').removeAttribute("hidden");
            // 타이머 이어서
            if (active == false) {
                active = true;
                timeoutId = setInterval(function () {
                    second++;
                    if (second > 59) {
                        second = 0;
                        minute++;
                        if (minute > 59) {
                            minute = 0;
                            hour++;
                            if (hour > 59) {
                                hour = 0;
                            }
                        }
                    }
                    document.getElementById("time").innerText =
                        (hour < 10 ? "0" + hour : hour) +
                        ":" +
                        (minute < 10 ? "0" + minute : minute) +
                        ":" +
                        (second < 10 ? "0" + second : second);
                }, 1000);
            }
            document.querySelector('img').setAttribute("hidden","true");
            document.querySelector('canvas').removeAttribute("hidden");
        }

    }
}

document.querySelector("#stop").setAttribute("hidden", "true");
document.querySelector("#restart").setAttribute("hidden", "true");
function stop(){

    camOFF = true;
    document.querySelector("#stop").setAttribute("hidden", "true");
    document.querySelector("#restart").removeAttribute("hidden");
}

async function restart(){
    camOFF = false;
    window.requestAnimationFrame(loop);
    active=false;
    if (active == false) {
        active = true;
        timeoutId = setInterval(function () {
            second++;
            if (second > 59) {
                second = 0;
                minute++;
                if (minute > 59) {
                    minute = 0;
                    hour++;
                    if (hour > 59) {
                        hour = 0;
                    }
                }
            }
            document.getElementById("time").innerText =
                (hour < 10 ? "0" + hour : hour) +
                ":" +
                (minute < 10 ? "0" + minute : minute) +
                ":" +
                (second < 10 ? "0" + second : second);
        }, 1000);
    }
    document.querySelector("#stop").removeAttribute("hidden");
    document.querySelector("#restart").setAttribute("hidden", "true");
}


let name="hihi";
$(document).ready(function () {

    findStudyUser();
    start();
});
function findStudyUser(){
    $.ajax({
        url: "/chat/findStudyUser",
        type: "GET",
        async: false,
        success: function(data){
            name = data;
        },error: function(error){
            console.log(error);
        }
    });
    $.ajax({
        url: "/chat/findTitle",
        type: "GET",
        async: false,
        data :{
            tID:curTID
        },
        success: function(data){
            let title = document.getElementById('title');
            title.innerText = data;
        },error: function(error){
            console.log(error);
        }
    });
}

function start(){
    var chatBox = $('.chat_box');
    var messageInput = $('input[name="message"]');
    var sendBtn = $('.send');
    var exitBtn = $('.exit');
    var roomId = $('.content').data('room-id');
    var member = name;
    //var member = $('.content').data('member');
    var sock = new SockJS("/stomp-chat");
    var client = Stomp.over(sock);
    client.debug = function (e) {
    };

    client.connect({}, function () {
        client.send('/publish/chat/join', {}, JSON.stringify({chatRoomId: curTID, type: 'JOIN', writer: member}));
        client.subscribe('/subscribe/chat/room/' + curTID, function (chat) {
            var content = JSON.parse(chat.body);
            // chatBox.append('<li>' + content.message + '(' + content.writer + ')</li>')
            if(content.message==content.writer+"님이 입장하셨습니다."){
                chatBox.append('<h4 style="color: blue">'+ content.message +'</h4>')
            }
            else if(content.writer==name){ //본인일 때
                chatBox.append('<h4 style="color: orange">'+ content.writer+ ' : ' + content.message +'</h4>')
            }
            else if(content.writer==""){
                chatBox.append('<h4 style="color: red">'+ content.message +'</h4>')
            }
            else{
                chatBox.append('<h4>'+ content.writer+ ' : ' + content.message +'</h4>')
            }

        });
    });

    sendBtn.click(function () {
        if(messageInput.val()!="") {
            var message = messageInput.val();
            client.send('/publish/chat/message', {}, JSON.stringify({
                chatRoomId: curTID,
                type: 'CHAT',
                message: message,
                writer: member
            }));
            messageInput.val('');
        }
    });

    exitBtn.click(function () {
        let c = confirm("스터디방을 나가시겠습니까?")
        if(c) {

            $.ajax({
                type: "POST",
                url: "/updateUserTeam",
                data: {data: false, realTime:realTime, totalTime:totalTime, tID: curTID},
                success: function (data) {
                },
                error: function (error) {
                    console.log(error);
                }
            });

            stop()
            client.send('/publish/chat/message', {}, JSON.stringify({
                chatRoomId: curTID,
                type: 'CHAT',
                message: member+"님이 채팅방을 나갔습니다.",
                writer: ""
            }));
            messageInput.val('');
            window.location.href = "/myPage";
        }

    });

    $(document).ready(function() {
        $("#alpreah_input").keydown(function(key) {
            if (key.keyCode == 13) {
                // alert("엔터키를 눌렀습니다.");
                if(messageInput.val()!="") {
                    var message = messageInput.val();
                    client.send('/publish/chat/message', {}, JSON.stringify({
                        chatRoomId: curTID,
                        type: 'CHAT',
                        message: message,
                        writer: member
                    }));
                    messageInput.val('');
                }
            }
        });
    });
}

function home(){
    $.ajax({
        type: "POST",
        url: "/updateUserTeam",
        data: {data: false, realTime:realTime, totalTime:totalTime, tID: curTID},
        success: function (data) {
        },
        error: function (error) {
            console.log(error);
        }
    });
    stop();

    window.location.href = "/";
}

/*
//임의로 만든 time()함수 함수명 바꾼다면 실행할 함수명도 같이 바꿔줘야지 작동합니
function time(seconds) {
    //3항 연산자를 이용하여 10보다 작을 경우 0을 붙이도록 처리 하였다.
    var hour = parseInt(seconds/3600) < 10 ? '0'+ parseInt(seconds/3600) : parseInt(seconds/3600);
    var min = parseInt((seconds%3600)/60) < 10 ? '0'+ parseInt((seconds%3600)/60) : parseInt((seconds%3600)/60);
    var sec = seconds % 60 < 10 ? '0'+seconds % 60 : seconds % 60;
    //연산한 값을 화면에 뿌려주는 코드
    // document.getElementById("time").innerHTML = hour+":"+min+":" + sec;
    return hour+":"+min+":" + sec;
}*/

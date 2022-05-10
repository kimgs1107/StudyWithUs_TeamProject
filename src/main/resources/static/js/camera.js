const URL = "/my_model/";

let model, webcam, webcamContainer, maxPredictions;
webcamContainer = document.querySelector("#webcam-container")

let curID;

let hour=0,minute=0,second=0,active=false,timeoutId,totalTime,realTime;
// Load the image model and setup the webcam
$.ajax({
    type: "POST",
    url: "getTotalTime",
    success: function (data) {
        console.log("total" + data)
        totalTime+=0
        totalTime=data*100;
    },
    error: function (error) {
        console.log(error);
    }
});
$.ajax({
    type: "POST",
    url: "getRealTime",
    success: function (data) {
        console.log("realTime" + data)
        realTime+=0
        realTime=data*100;
    },
    error: function (error) {
        console.log(error);
    }
});
$.ajax({
    type: "POST",
    url: "/getLoginUser",
    success: function (data) {
        curID = data;
    },
    error: function (error) {
        console.log(error);
    }
});
async function init() {
    document.querySelector("#start").setAttribute("hidden","true");
    document.querySelector("#stop").removeAttribute("hidden");
    document.querySelector("#restart").removeAttribute("hidden");

    let webSocket = new WebSocket("ws://"+window.location.host+"/exist");
    webSocket.onopen = function(event){
        webSocket.send(curID); //나중엔 팀 정보를 같이 넘겨줘야함
    }
    webSocket.onmessage = function(event){
        console.log("In onmessage");

        var mess = event.data.split(" ");
        let id = document.getElementById(mess[0]);
        id.innerHTML = event.data;
        console.log(event.data);
    }

    var members = document.querySelector("#members");
    $.ajax({
        type: "POST",
        url: "/members",
        data: {tID: 1},
        success: function (data) {
            data.forEach(function(item){
                let id = document.createElement("div");
                id.innerHTML = item+" off";
                id.setAttribute("id", item);

                members.append(id);
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

    // 타이머
    // if (active == false) {
    //     active = true;
    //     timeoutId = setInterval(function () {
    //         second++;
    //         if (second > 59) {
    //             second = 0;
    //             minute++;
    //             if (minute > 59) {
    //                 minute = 0;
    //                 hour++;
    //                 if (hour > 59) {
    //                     hour = 0;
    //                 }
    //             }
    //         }
    //         document.getElementById("time").innerText =
    //             (hour < 10 ? "0" + hour : hour) +
    //             ":" +
    //             (minute < 10 ? "0" + minute : minute) +
    //             ":" +
    //             (second < 10 ? "0" + second : second);
    //     }, 1000);
    // }
}

var camOFF = false;
async function loop() {
    if(camOFF) {
        clearInterval(timeoutId);
        window.cancelAnimationFrame(loop);
        $.ajax({
            type: "POST",
            url: "/updateUserTeam",
            data: {data: false, realTime:realTime,totalTime:totalTime},
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
    await predict();
    window.requestAnimationFrame(loop);
}

let cnt=0;
let data;
async function predict() {
    const prediction = await model.predict(webcam.canvas);
    for (let i = 0; i < maxPredictions; i++) {

        if(prediction[1].probability.toFixed(2) < 0.2){ // 자리이탈
            cnt++;
            if(cnt<=500){
                realTime++;
                totalTime++
            }
        }

        if(cnt>500){ // 100 = 1초  // 30000=300초=5분 // 테스트는 10초로
            clearInterval(timeoutId);
            active = false;
            data=false;
            $.ajax({
                type: "POST",
                url: "updateUserTeam",
                data: {data: data, realTime:realTime,totalTime:totalTime},
                success: function (data) {     },
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
                type: "POST",
                url: "updateUserTeam",
                data: {data: data,realTime:realTime,totalTime:totalTime},
                success: function (data) {
                },
                error: function (error) {
                    console.log(error);
                }
            });

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

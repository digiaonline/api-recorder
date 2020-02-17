$(document).ready(function(){
    $("#listrecordings").click(function(){listRecordings()});
    $("#listplayers").click(function(){listPlayers()});
    setInterval(refreshPlayerList, 5000);
})

function listRecordings(){
    $.get("/api/record/list", function(data, status){buildRecordList(data, status);});
}

function refreshPlayerList(){
    if($("#playerTable").length > 0){
        listPlayers();
    }
}

function listPlayers(){
    $.get("/api/player/list", function(data, status){buildPlayerList(data, status);});
}

var records;

function buildRecordList(data, status){
    var newHtml = "<table id='recordTable'><tr><th>Name</th><th>Start (UTC)</th><th>End (UTC)</th><th>uuid</th><th>Definition</th><th></th></tr>";
    for(var i = 0 ; i < data.length ; i++){
        var recordItem = data[i];
        newHtml += "<tr><td>" + recordItem.name + "</td><td>" + recordItem.start + "</td><td>" + recordItem.end + "</td><td>" + recordItem.uuid + "</td><td><a href='#' onclick=\"displayDefinition(" + i + ")\" > Show</a></td><td><button id='createplayer' onclick=\"createPlayer('" + recordItem.uuid + "')\">Create a player</button><button id='deleterecording'>Delete</button></td></tr>";
    }
    newHtml += "</table>";
    records = data;
    $("#content").html(newHtml);
}

function displayDefinition(i){
    var newWindow = window.open("");
    newWindow.document.write(records[i].definition);
}

function createPlayer(recordUuid){
    var playerUuid = prompt("Input player uuid or leave empty for auto generation", "");
    if(playerUuid != null){
        var playerCreationDTO = new Object();
        playerCreationDTO.recordUuid = recordUuid;
        if(playerUuid != ""){
            playerCreationDTO.playerUuid = playerUuid;
        }
        $.ajax({
            method: "POST",
            url: "/api/player/create",
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            data: JSON.stringify(playerCreationDTO)
        });
        alert("Player created for record " + recordUuid);
    }
}

function buildPlayerList(data, status){
    var newHtml = "<table id='playerTable'><tr><th>Player uuid</th><th>Prefix</th><th>Record uuid</th><th>Speed</th><th>Current offset</th><th></th></tr>";
    for(var i = 0 ; i < data.length ; i++){
        var playerItem = data[i];
        newHtml += "<tr><td>" + playerItem.playUuid + "</td><td>" +  window.location.hostname + "/watch/" + playerItem.playUuid + "/url/</td><td>" + playerItem.recordUuid + "</td><td>" + playerItem.speed + "</td><td>" + playerItem.currentOffset + "</td><td><button id='jumpToOffset' onclick=\"jumpToOffset('" + playerItem.playUuid + "')\">Jump to offset</button><button id='stop' onclick=\"stop('" + playerItem.playUuid + "')\">Stop</button><button id='playpause' onclick=\"playPause('" + playerItem.playUuid + "', " + playerItem.speed + ")\">Play/Pause</button><button id='speedup' onclick=\"changeSpeed('" + playerItem.playUuid + "', " + playerItem.speed + ", +1)\">Speed up</button><button id='speeddown' onclick=\"changeSpeed('" + playerItem.playUuid + "', " + playerItem.speed + ", -1)\">Speed down</button><button id='deleteplayer'>Delete</button></tr>";
    }
    newHtml += "</table>";
    $("#content").html(newHtml);
}

function jumpToOffset(playerUuid){
    var offset = prompt("Input new offset", "0");
    if(offset != null){
        $.ajax({
            method:'PUT',
            url: '/api/player/' + playerUuid + '/offset/' + offset
        }).done(function (data){listPlayers();});
        listPlayers();
    }

}

function stop(playerUuid){
    $.ajax({
        method:'PUT',
        url: '/api/player/' + playerUuid + '/stop'
    }).done(function (data){listPlayers();});
}

function playPause(playerUuid, currentSpeed){
    if(currentSpeed != 0){
        $.ajax({
            method:'PUT',
            url: '/api/player/' + playerUuid + '/pause'
        }).done(function (data){listPlayers();});
    }
    else{
        $.ajax({
            method:'PUT',
            url: '/api/player/' + playerUuid + '/play'
        }).done(function (data){listPlayers();});
    }
}

function changeSpeed(playerUuid, currentSpeed, diff){
    $.ajax({
        method:'PUT',
        url: '/api/player/' + playerUuid + '/speed/' + (currentSpeed + diff)
    }).done(function (data){listPlayers();});
}

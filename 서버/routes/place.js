var express = require('express');
var mysql = require('mysql');
var request = require('request');
var router = express.Router();
var connection = mysql.createConnection({
  'host': 'aws-rds-mysql.cjj4qeglpmie.us-west-2.rds.amazonaws.com',
  'user': 'user',
  'password': 'rlawlsrb1!',
  'database': 'TravelPlanner',
});

/*
 * Method       : POST
 * Path           : http://52.34.206.80:3000/place/add
 * Description  : 사용자가 장소를 추가합니다.
 */
router.post('/add', function (req, res, next) {

  var userId = req.body.id;
  var planName = req.body.planname;
  var itemInfo = req.body.item;
  var itemLength = req.body.item.length;

  var routeDatas = [];
  var listOfIndex = [];
  var totalCallFuncCnt;

  var routeAPI = function (itemInfo) {
    routeDatas = [];
    listOfIndex = [];
    totalCallFuncCnt = 0;

    var startX = itemInfo[0].mapx;
    var startY = itemInfo[0].mapy;
    var endX = itemInfo[itemLength - 1].mapx;
    var endY = itemInfo[itemLength - 1].mapy;

    // 장소가 2개일 때
    if (itemLength == 2) {
      totalCallFuncCnt = 1;
      _getPropertyOfLocations(startX, startY, endX, endY, listOfIndex);

    } else {
      var indexOfDatas = [];

      // 장소가 저장된 배열에서 출발, 도착지점을 제외한 장소의 Index 저장
      for (var i = 1; i < itemLength - 1; i++) {
        indexOfDatas.push(i);
      }

      console.log("indexOfDatas = ", indexOfDatas);

      // 경우의 수 구하기
      _perm(indexOfDatas, 0, indexOfDatas.length, indexOfDatas.length);

      // 모든 경우의 수로 경로 구하기
      for (var i = 0; i < listOfIndex.length; i++) {
        totalCallFuncCnt++;
        _getPropertyOfLocations(startX, startY, endX, endY, listOfIndex[i]);
      }

      // debug
      //console.log("-----listOfIndex -----");
      //console.log(orderOfIndex);

      //test
      //console.log("-----listOfIndex[0])-----");
      //console.log(orderOfIndex[0]);
      //console.log("-----listOfIndex[0][0])-----"); console.log(orderOfIndex[0][0]);
      //console.log("-----listOfIndex[0][1])-----"); console.log(orderOfIndex[0][1]);
      //console.log("-----listOfIndex[0][2])-----"); console.log(orderOfIndex[0][2]);
      //console.log("-----listOfIndex[0][3])-----"); console.log(orderOfIndex[0][3]);
      //console.log("-----좌표 Test-----");
      //console.log("mapx = ", itemInfo[listOfIndex[0][1]].mapx);
      //console.log("mapy = ", itemInfo[OfIndex[0][1]].mapy);
    }
  };

  var _perm = function (arr, depth, n, k) {
    if (depth == k) {
      _printf(arr, k);
      return;
    }

    for (var i = depth; i < n; i++) {
      _swap(arr, i, depth);
      _perm(arr, depth + 1, n, k);
      _swap(arr, i, depth);
    }
  }

  var _swap = function (arr, i, j) {
    var temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  var _printf = function (arr, k) {
    var tmp = [];

    for (var i = 0; i < k; i++) {
      if (i == (k - 1)) {
        tmp.push(arr[i]);
        listOfIndex.push(tmp);
        //console.log(tmp);
      } else {
        tmp.push(arr[i]);
      }
    }
  }

  var _getPropertyOfLocations = function (startX, startY, endX, endY, orderOfIndex) {
    //Lets configure and request

    // passList 만들기
    var passList = "";

    for (var i = 0; i < orderOfIndex.length; i++) {
      passList = passList + itemInfo[orderOfIndex[i]].mapx + ','
        + itemInfo[orderOfIndex[i]].mapy + ','
        + "0,0,0_";
    }

    // debug
    //console.log("passList =", passList);

    request({
      url: 'https://apis.skplanetx.com/tmap/routes?callback=&version=1',
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'appKey': '7ee755f7-eed9-3096-ab67-7083094711c9'
      },
      //Lets post the following key/values as form
      form: {
        startX: startX,
        startY: startY,
        endX: endX,
        endY: endY,
        reqCoordType: 'WGS84GEO',
        passList: passList
      }
    }, function (error, response, body) {
      if (error) {
        console.log(error);
      } else {
        time = JSON.parse(body).features[0].properties.totalTime;
        distance = JSON.parse(body).features[0].properties.totalDistance;
        property = {
          orderOfIndex: orderOfIndex,
          time: time, distance: distance
        };

        routeDatas.push(property);

        if (routeDatas.length == totalCallFuncCnt) {
          console.log(routeDatas);
          console.log("routeDatas.length = " + routeDatas.length);
          console.log("----------모든 장소루트의 거리 시간 받아옴----------");

          findFastestRoute();
        }
      }
    });
  };

  var findFastestRoute = function () {
    var fastestRoute = routeDatas[0];

    for (var i = 1; i < routeDatas.length; i++) {
      if (fastestRoute.time > routeDatas[i].time) {
        fastestRoute = routeDatas[i];
      }
    }

    /* test
     // orderOfIndex 맨 앞과 맨 뒤에 DB에 들어갈 porder 넣기
     fastestRoute.orderOfIndex.unshift(0);
     fastestRoute.orderOfIndex.push(itemLength-1);
     */

    //debug
    console.log("fastestRoute =", fastestRoute);

    insertToDB(fastestRoute);
  };

  var insertToDB = function (fastestRoute) {
    var insertCnt = 0;
    var tmpOrderOfIndex = fastestRoute.orderOfIndex;
    var time = fastestRoute.time;
    var distance = fastestRoute.distance;

    // orderOfIndex 맨 앞과 맨 뒤에 DB에 들어갈 porder 넣기
    tmpOrderOfIndex.unshift(0);
    tmpOrderOfIndex.push(itemLength - 1);

    connection.query('delete from Place where id=? && planname=?;', [
      userId,
      planName
    ], function (error, info) {
      if (!error) {

        // debug
        console.log("--- 출발지 : " + itemInfo[0].placename + "---");
        console.log("--- 도착지 : " + itemInfo[itemLength - 1].placename + "---\n");

        for (var i = 0; i < itemLength; i++) {
          connection.query('insert into Place (id, planname, placename, address, contentid, contenttypeid, mapx, mapy, imgpath, porder) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);',
            [
              userId,
              planName,
              itemInfo[i].placename,
              itemInfo[i].address,
              itemInfo[i].contentid,
              itemInfo[i].contenttypeid,
              itemInfo[i].mapx,
              itemInfo[i].mapy,
              itemInfo[i].imgpath,
              tmpOrderOfIndex[i]
            ],
            function (error, cursor) {
              if (!error) {
                console.log("DB Insert");

                if (++insertCnt == itemLength) {
                  console.log("----------모든 장소 DB 저장 완료----------");

                  connection.query('update PlanList set alldistance=?, alltime=? where id=? && name=?;',
                    [distance, time, userId, planName], function (error, info) {
                      console.log("----------플랜 시간 거리 DB 저장 완료----------");
                      responseToClient(200);
                    });

                  //        connection.query('update Confirm set confirmkey=? where email=?', [confirmkey,
                  // req.body.email], function (error, cursor) {
                }

              } else {
                console.log(error);
                responseToClient(400);
              }
            });
        }
      }
    });
  };

  var responseToClient = function (rescode) {
    res.status(rescode).json({result: true});
    console.log("----------서버 처리 완료----------\n");
  };

  routeAPI(itemInfo);
});

/*
 * Method       : POST
 * Path           : http://52.34.206.80:3000/place/load
 * Description  : 사용자가 장소를 장소를 불러옵니다.
 */
router.post('/load', function (req, res, next) {

  // debug
  console.log("------------- req ------------- \n" + req);
  // console.log(req);

  var userId = req.query.id;
  var planName = req.query.planname;

  // debug
  console.log("userId : " + userId);
  console.log("planName : " + planName);

  connection.query('select * from Place where id=? && planname=? order by porder;', [
    userId,
    planName
  ], function (error, cursor) {
    if (cursor.length == 0) {
      // DB에 저장된 플랜의 장소가 없음
      res.status(404).json();
    } else {
      // debug
      // console.log(cursor);
      // DB에 저장된 플랜의 장소가 있음
      res.status(200).json(cursor);
    }
  });

});

module.exports = router;

var express = require('express');
var mysql = require('mysql');
var fs = require('fs');
var bodyParser = require('body-parser');
var router = express.Router();

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});


router.get('/:id', function(req, res, next) {
    // 그냥 board/ 로 접속할 경우 전체 목록 표시로 리다이렉팅
    connection.query('delete from Facility where Facility_id = ?;', 
                    [req.params.id ],
                     function(error){
        if(!error){
            res.redirect('/facil/del');
        }
        console.log("123");
        if(error){
            res.status(503);
        }
    });
   
});
module.exports=router;
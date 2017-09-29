var express = require('express');
var mysql = require('mysql');
var async = require('async');
var router = express.Router();
var pt;

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});

router.post('/',function(req,res,next){
    var id =req.body.user_id;
    var pw = req.body.password;
    if(id == "admin@bodeum.com" && pw == "1234")
        res.redirect('/join');
    else 
        res.end("login fail!!");
});


module.exports=router;
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
router.post('/mup12/:id', function (req, res) {
    connection.query('update Member set Member_pass=?, Member_phone=?, Member_nick=? where Member_em=?;',[ "123", "123", "123", req.params.id ],
                    function(error){
        if(!error){
            res.redirect('/mboard');
        }
        console.log("123");
        if(error){
            res.status(503);
        }
    });
});



module.exports=router;
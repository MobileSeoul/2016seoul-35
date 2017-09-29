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


router.post('/:id', function(req, res, next){ 
        connection.query('select * from Member where Member_email=?;', [req.params.id], function (error, cursor){
            if (cursor.length > 0) { 
                res.json(cursor);
            }
            else
                res.status(503).json({ result : false, reason : "Cannot post article"});
        });
});

router.post('/writing/:id',function(req, res, next){
    connection.query('select * from Comm where Member_email=?;', [req.params.id], function (error, cursor){
        if (cursor.length > 0) { 
                res.json(cursor);
            }
        else
            res.status(503).json({ result : false, reason : "Cannot post article"});
        });
});


module.exports=router;
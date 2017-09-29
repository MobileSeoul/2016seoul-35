

var express = require('express');
var mysql = require('mysql');
var async = require('async');
var router = express.Router();
var pt;
var flag=0;

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});
router.post('/', function(req,res){
    connection.query('select * from love where Love_email =? AND Love_Comm_id = ?;', [req.body.Member_email, req.body.Comm_id], function(error, col){
        if(error == null && col.length==0){
            connection.query('insert into love(Love_email, Love_Comm_id) values(?,?);', [req.body.Member_email, req.body.Comm_id], function(){
                connection.query('update Comm set Comm_like=Comm_like+1 where Comm_id=?;', [req.body.Comm_id], function(err){
                    if(err==null) {
                        console.log("like succes");
                        res.status(200).json({result:true});
                    }
                    else res.status(503).json({code:"like error", erno:"201"});
                });
                
            });
            
        }
        else if(error == null && col.length>0){
            connection.query('delete from love where Love_email=? AND Love_Comm_id=?;',[req.body.Member_email, req.body.Comm_id],function(){    
                connection.query('update Comm set Comm_like=Comm_like-1 where Comm_id= ?;',[req.body.Comm_id],function(err){
                    if(err==null) {
                        console.log("like del success");
                        res.status(200).json({result:true});
                    }
                    else res.status(503).json({code: "del like error", erno:"202"});
                });
            });
        }
        else res.status(503).json({code:"first like query error", erno:"200"});
    });
    
});



module.exports=router;

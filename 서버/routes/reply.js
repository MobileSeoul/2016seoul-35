var express = require('express');
var mysql = require('mysql');
var router = express.Router();

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});

router.post('/',function(req,res,next){
console.log(req.body);
    connection.query('insert into Reply(Reply_text, Comm_Comm_id, Member_Member_email) values (?,?,?);',[req.body.Reply_text, req.body.Comm_Comm_id, req.body.Member_Member_email],
                     function(error, info){
             if(error==null){
        res.status(200).json({result:true});
     }
        else
           res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
         });
    
        connection.query('select count(*) as cnt from Reply where Comm_Comm_id =?;',[req.body.Comm_Comm_id], function(error, cursor){
        if(error==null){
            var cnt = cursor[0].cnt;
            connection.query('update Comm set Comm_reply = ? where Comm_id = ?;',[cnt, req.body.Comm_Comm_id],function (error, info){
                if(error==null){
                    
                }else
                    res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
            });
        }else
            res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
    });
});

router.post('/del',function(req,res,next){
    connection.query('select * from Reply where Reply_id =?',[req.body.Reply_id], function(error, rows){
        if(error == null){
                                res.status(200).json({result:true});

            if(rows[0].Member_Member_email == req.body.Member_Member_email){
                connection.query('delete from Reply where Reply_id =?;',[req.body.Reply_id],
                     function(error, info){
                        if(error==null){
                            console.log("success");     
                            }else
                                res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
                });
    
    connection.query('select count(*) as cnt from Reply where Comm_Comm_id =?;',[req.body.Comm_Comm_id], function(error, cursor){
        if(error==null){
            var cnt = cursor[0].cnt;
            connection.query('update Comm set Comm_reply = ? where Comm_id = ?;',[cnt, req.body.Comm_Comm_id],function (error, info){
                if(error==null){
                }else
                    res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
            });
        }else
            res.status(503).json({result:false,code:"Error Reply insert",erno:"112"});
    
    });
                }else
                    res.status(503).json({code:"Error Review del",erno:"106"});
        }else{
            res.status(503).json({code:"Error Review del",erno:"106"});
        }
    });   
});

router.get('/list/:Comm_Comm_id', function(req,res){
console.log(req.params);    
connection.query('select * from Reply where Comm_Comm_id = ? order by Reply_time asc;' ,[req.params.Comm_Comm_id], function(error, cursor){
        if(cursor.length>0){
console.log(cursor);
            res.json(cursor);
        }else
       res.status(503).json({code:"Error Reply list",erno:"115"});
    });

}); 

module.exports=router;

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
console.log(req.body);   
 connection.query('insert into Comm(Comm_title, Comm_content, Comm_photo, Member_email) values (?,?,?,?);',[req.body.Comm_title, req.body.Comm_content, "test1.jpg", req.body.Member_email],
                     function(error, info){
             if(error==null){
             res.status(200).json({result:true});
     }
        else
           res.status(503).json({result:false,code:"Error Comm insert",erno:"110"});
         });
});

router.get('/totallists/:id', function(req,res){
console.log(req.params);
connection.query('select Comm_id, Comm_title, Comm_content,Member.Member_nick, Comm_time, Comm_photo, Comm.Member_email, Comm_like, Comm_reply, Member_photo, (select Love_Comm_id from love where love.Love_Comm_id=Comm.Comm_id and love.Love_email=?) as Like_Comm from Member, Comm where Member.Member_email = Comm.Member_email order by Comm_time desc;',[req.params.id], function(error, cursor){       
 if(cursor.length>0){
//console.log(cursor);
            res.json(cursor);
        }else
            res.status(503).json({code:"Error Comm insert",erno:"111"});
    });
});

       
router.get('/lists/:id/:email', function(req,res){//사진추가 바꾸기

connection.query('select count(*) as cnt from Reply where Comm_Comm_id =?;',[req.params.id], function(error, cursor){
        if(error==null){
            var cnt = cursor[0].cnt;
            connection.query('update Comm set Comm_reply = ? where Comm_id = ?;',[cnt, req.params.id],function (error, info){
                if(error==null){
		  connection.query('select Comm_title, Comm_content, Comm_time, Comm_photo, Member_nick,Member_photo, Comm_like, Comm_reply, (select Love_Comm_id from love where love.Love_Comm_id=Comm.Comm_id and love.Love_email=?) as Like_Comm from Comm,Member where Comm.Member_email = Member.Member_email and Comm_id = ?;',[req.params.email, req.params.id], function(error,rows){                     if(rows.length>0)
                res.json(rows[0]);
        else
          res.status(503).json({result : false,code:"No Review",erno:"107"});
    });


                }else
                    res.status(503).json({result:false,code:"Error Reply num",erno:"112"});
            });
        }else
            res.status(503).json({result:false,code:"Error Reply num",erno:"112"});
    });
});



//connection.query('select Comm_title, Comm_content, Comm_time, Comm_photo, Member_nick,Member_photo, Comm_like, Comm_reply, (select Love_Comm_id from love where love.Love_Comm_id=Comm.Comm_id and love.Love_email=?) as Like_Comm from Comm,Member where Comm.Member_email = Member.Member_email and Comm_id = ?;',[req.params.email, req.params.id], function(error,rows){                     if(rows.length>0)         
  //      	res.json(rows[0]);
    //    else
//	  res.status(503).json({result : false,code:"No Review",erno:"107"});     
  //  });
//}); 

        
router.get('/mlist/:search', function(req, res){
	var search=req.params.search;
    search = '%'+search+'%';
    
    connection.query('select Comm_id, (select Member_nick from Member where Comm.Member_email=Member.Member_email) as Comm_nic, Comm_title, Comm_photo, Comm_time, Comm_like, (select count(*) from Reply where Member_Member_email = Comm.Member_email) as Comm_reply from Comm where Comm_title like ? order by Comm_time desc;',[search], function(error, cursor){	
        //console.log(cursor);
        res.json(cursor);
    });
});

router.get('/mlist', function(req, res, next) { 
	connection.query('select Comm_id, (select Member_nick from Member where Comm.Member_email=Member.Member_email) as Comm_nic, Comm_title, Comm_photo, Comm_time, Comm_like, Comm_reply from Comm order by Comm_time desc;',function(error, cursor){	
		//console.log(cursor);
		res.json(cursor);
	});
});

router.get('/list/', function(req, res) {
    connection.query('select * from Comm;',function(error, rows){
       res.render('comm_list',{rows: rows}, function(error, content) {
            if(!error) {
                res.end(content);
            } else {
                res.writeHead(501, { 'Content-Type' : 'text/plain' });
                res.end('Error while reading a file');
            }
        });
    });
});
router.get('/delete/:id', function(req, res, next) {
    connection.query('delete from Comm where Comm_id = ?;', [req.params.id], function(error){
        if(!error)
            res.redirect('/comm/list');

        else
            res.status(503);

    });
});

router.get('/update/:id', function(req, res, next) {
    connection.query('select * from Comm where Comm_id=?;', [req.params.id], function (error, cursor){
        if (cursor.length > 0) { 
                //res.json(cursor);
                res.render('comm_edit', {cursor : cursor});
            }
        else
            res.status(503).json({ result : false, reason : "Cannot post article"});
    });
});

router.post('/update_complete/:id', function (req, res) {
    connection.query('update Comm set Comm_title=?, Comm_content=? where Comm_id=?;',[ req.body.title, req.body.content, req.params.id],
                    function(error){
        if(!error){
            res.redirect('/comm/list');
        }
        if(error){
            res.status(503);
        }
    });
});

router.post('/cdel',function(req,res,next){
    connection.query('select * from Comm where Comm_id =?',[req.body.Comm_id], function(error, rows){
        if(error == null){
            if(rows[0].Member_email == req.body.Member_email){
                connection.query('delete from Comm where Comm_id =?;',[req.body.Comm_id],
                     function(error, info){
                        if(error==null){
                            res.status(200).json({result:true});   
                            }else
                                res.status(503).json({result : false,code:"Error Review del",erno:"105"});
                });
             }else
                    res.status(503).json({result : false,code:"Error Comm del",erno:"106"});
        }else{
            res.status(503).json({result : false,code:"Error Comm del",erno:"106"});
        }
    });   
});  

router.post('/cupdate',function (req, res){
    connection.query('select * from Comm where Comm_id=?',[req.body.Comm_id], function(error, rows){
        if(error == null){
            if(rows[0].Member_email == req.body.Member_email){
               // res.json(rows);
                connection.query('update Comm set Comm_title =?,Comm_content =?, Comm_photo=? where Comm_id =?;', [req.body.Comm_title, req.body.Comm_content, req.body.Comm_photo, req.body.Comm_id],function(error){
                    if(!error){
                        res.status(200).json({result:true});
                    }else
                        res.status(503).json({result : false,code:"Comm Update Fail",erno:"108"});
                });
            }else
                res.status(503).json({result : false,code:"Error Comm Update",erno:"106"});
        }else
            res.status(503).json({result : false,code:"Error Comm Update",erno:"106"});
    });
});


    
    
    module.exports=router;

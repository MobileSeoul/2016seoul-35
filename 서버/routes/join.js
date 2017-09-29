var express = require('express');
var mysql = require('mysql');
var router = express.Router();
var fs = require('fs');

var aaa;

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});

router.get('/', function(req, res) {
   res.render('index', function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
router.get('/login_admin/', function(req,res){
    res.render('login', function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });   
});


router.get('/email/:email',function(req, res){
 var check=1;
    connection.query('select * from Member;',function(error, cursor){

        if(error==null){

            for(var i=0; i<cursor.length; i++){
                if(cursor[i].Member_email==req.params.email){

                    check=0;
                }

            }
            if(check == 0){
                res.status(503).json({result:false, reson:"Exist Email"});
            }else
                res.status(200).json({result:true});
        }else{

            res.status(503).json({result:false});
        }
                                     
   });
});
router.get('/nic/:nic',function(req, res){
 var check=1;
    connection.query('select * from Member;',function(error, cursor){
        
        if(error==null){
            
            for(var i=0; i<cursor.length; i++){
                if(cursor[i].Member_nick==req.params.nic){
                    
                    check=0;
                }
                                   
            }
            if(check == 0){
                res.status(503).json({result:false, reson:"Exist NickName"});
            }else
                res.status(200).json({result:true});
        }else{
           
            res.status(503).json({result:false});
        }
    });
 
});

router.post('/login',function(req,res,next){
    console.log(req.body);
    connection.query('select * from Member where Member_email =?',[req.body.Member_email],function(error, rows){
        if(!error){
            var er;
            if(rows.length>0)
                {
            if(req.body.Member_pass == rows[0].Member_pass){
                res.status(200).json(rows[0]);
            }else
                res.status(503).json({result:false});
                }
            else
                res.status(503).json({result:false, reson:"no ID"});
        }else
            res.status(503).json({result:false});
    });
});

//회원가입 시 요청 부분
router.post('/',function(req,res,next){
    var er;
     connection.query('select Member_email from Member;', function(error, rows){
         for(var i=0; i<= rows.length; i++){
                     if(req.body.Member_email = rows[i]){
                         er = 1;   
                     }
         }
    });
    if(er == 1)
        res.status(503).json({code:"exist ID",erno:"101"});

    else
        connection.query('insert into Member(Member_pass, Member_name, Member_email, Member_nick) values (?,?,?,?);',[req.body.Member_pass, req.body.Member_name, req.body.Member_email, req.body.Member_nick],
                     function(error, info){
            if(error==null){
                res.status(200).json({result: true});
            }else
                res.status(503).json({result:false});
        });
});

//회원탈퇴 시 요청 부분
router.get('/out/:id',function(req,res,next){
	connection.query('delete from Member where Member_email = ?;',[req.params.id],
                     function(error, info){
            if(error==null){
                res.status(200).json({result: true});
            }else
                res.status(503).json({result:false});
        });
});


router.get('/facility', function(req,res){
    connection.query('select * from Facility', function(error, cursor){
            if(cursor.length>0){
                res.json(
                    cursor
                );
            }
            else
                res.status(503).json({code:"Can't Receive",erno:"109"});
        });
});

router.get('/member/:id',function(req,res,next){
    connection.query('select * from Member where Member_email =?',[req.params.id],function(error, rows){
        if(error==null){
         res.json(rows);
                    }
        else
            res.status(503).json({result:false});        
    });
});

router.post('/favorite',function(req,res){
    connection.query('select * from Favorite where Member = ?',[req.query.Member],function(error,rows){

var a = 0;  
        if(error==null){
            for(var i=0; i<rows.length; i++){
             
                if(rows[i].Facility == req.query.Facility){
                    a=1;
console.log("change");}
            }
            if(a ==1){
console.log(a);
                connection.query('delete from Favorite where Facility=? and Member =?;',[req.query.Facility, req.query.Member],function(error, info){
                    if(error==null){
                        res.status(200).json({result: true, code:"del"});
                    }else
                        res.status(503).json({result:false,code:"favorite fail",erno:"118"});
                });
                }else{
console.log(a);
                    connection.query('insert into Favorite(Member,Facility) values (?,?)',[req.query.Member,req.query.Facility], function(error, info){
                        if(error==null){
                            res.status(200).json({result: true});
                            }else
                                res.status(503).json({result:false,code:"favorite fail",erno:"118"});
                        });
                }
            
            }else{
                connection.query('select * from Favorite where Member = ?',[req.query.Member],function(error,cursor){
 var b =0;                   
                    if(cursor.length>0){
                        for(var i=0; i<cursor.length; i++){
                    
        if(cursor[i].Facility == req.query.Facility)
                                b=1;
                            else
                                b=0;
                            }
                        if(b ==1){
console.log("test"+b);
                            connection.query('delete from Favorite where Facility=? and Member =?;',[req.query.Facility, req.query.Member],function(error, info){
                                if(error==null){
                                    res.status(200).json({result: true, code:"del"});
                                    }else
                                        res.status(503).json({result:false,code:"favorite fail",erno:"118"});
                        });
                        }
                        else{
console.log("tests"+b);
                            connection.query('insert into Favorite(Member,Facility) values (?,?)',[req.query.Member,req.query.Facility], function(error, info){
                                if(error==null){
                                    res.status(200).json({result: true});
                                    }else
                                        res.status(503).json({result:false,code:"favorite fail",erno:"118"});
                                });
                            }
                        }else
                            res.status(503).json({result:false});
                    });
                }
        });
    });
 

router.get('/favorite/:id',function(req,res){
    connection.query('select Facility_id,Facility_name,Facility_count,Facility_addr,Facility_call,Facility_avg,Facility_env,Facility_mang,Facility_sour,Facility_pro,Facility_chil,Facility_photo,Facility_aut,Facility_ecount,Facility_reviewnum,Facility_kind,Facility_score,(select Facility from Favorite where Favorite.Facility=Facility.Facility_id and Favorite.Member=?) as Favorite_facil  from Facility, Favorite where Facility.Facility_id = Favorite.Facility and Favorite.Member =?',[req.params.id,req.params.id],function(error, cursor){
        if(cursor.length>0){
            res.json(cursor);
        }else
            res.status(503).json({result:false,code:"Can't Receive",erno:"119"});
    });
});


router.post('/mupdate',function (req, res){
    connection.query('select * from Member where Member_email=?',[req.body.Member_email], function(error, rows){
        if(error == null){
            if(rows[0].Member_email == req.body.Member_email){
                connection.query('update Member set Member_pass =?, Member_name =?, Member_gender=?, Member_birth=?, Member_phone=?, Member_nick=?, Member_photo=? where Member_email =?;',[req.body.Member_pass, req.body. Member_name, req.body.Member_gender, req.body.Member_birth, req.body.Member_phone,req.body.Member_nick, req.body.Member_photo, req.body.Member_email],function(error){
                    if(!error){
                        res.status(200).json({result:true});
                    }else
                        res.status(503).json({result:false,code:"Member Update Fail",erno:"108"});
                });  
            }else
                res.status(503).json({result:false,code:"Error Member Update",erno:"106"});
        }else
            res.status(503).json({result:false,code:"Member Comm Update",erno:"106"});
    });
});
 

//내가 쓴 글 목록 불러오기
router.get('/mylist/:id',function(req,res,next){

    connection.query('select Comm.Comm_title, Member.Member_nick from Member,Comm where Comm.Member_email=Member.Member_email and Member.Member_email =?;',[req.params.id],function(error,rows){
        console.log(rows);
        res.json(rows);
    });
});

module.exports=router;


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
var test = "test.jpg";
    connection.query('insert into Review(Review_title, Review_content,  Review_photo, Member_emails, Facility_ids, Review_score) values (?,?,?,?,?,?);',[req.body.Review_title, req.body.Review_content, test, req.body.Member_emails, req.body.Facility_ids,req.body.Review_score],
                     function(error, info){
             if(error==null){
       res.status(200).json({result: true});  
     }
        else
           res.status(503).json({result:false,code:"Error Review",erno:"103"});
            
         });
    
    connection.query('select count(*) as cnt from Review where Facility_ids =?;',[req.body.Facility_ids], function(error, cursor){
        if(error==null){
            var cnt = cursor[0].cnt;
            connection.query('update Facility set Facility_reviewnum = ? where Facility_id = ?;',[cnt, req.body.Facility_ids],function (error, info){
                if(error==null){
                    
                }else
                    res.status(503).json({code:"Error Review update",erno:"104"});
            });
        }else
            res.status(503).json({result:false});
        
    connection.query('select * from Review where Facility_ids =?;',[req.body.Facility_ids], function(error, cursor){
        if(error==null){
            var score=1;
            for(var i=0; i<cursor.length; i++){
                //console.log(cursor[i].Review_score);
                
                score = score + cursor[i].Review_score;
            }
//            console.log("total" + score+ "cnt"+cnt);
            score= score/cnt;
//            console.log(score);
             connection.query('update Facility set Facility_score = ? where Facility_id = ?;',[score, req.body.Facility_ids],function (error, info){
                if(error==null){
                    
                }else
                    res.status(503).json({code:"Error Review update",erno:"104"});
            });
        }else
             res.status(503).json({code:"Error Review update",erno:"104"});
    });
            
            
    });
});
    
router.post('/del',function(req,res,next){
    connection.query('select * from Review where Review_id =?',[req.body.Review_id], function(error, rows){
        if(error == null){
            if(rows[0].Member_emails == req.body.Member_emails){
                connection.query('delete from Review where Review_id =?;',[req.body.Review_id],
                     function(error, info){
                        if(error==null){
                            res.status(200).json({result:true});     
                            }else
                                res.status(503).json({result:false,code:"Error Review del",erno:"105"});
                });
    
    connection.query('select count(*) as cnt from Review where Facility_ids =?;',[req.body.Facility_ids], function(error, cursor){
        if(error==null){
            var cnt = cursor[0].cnt;
            connection.query('update Facility set Facility_reviewnum = ? where Facility_id = ?;',[cnt, req.body.Facility_ids],function (error, info){
                if(error==null){
                    
                }else
                    res.status(503).json({result:false,code:"Error Review del",erno:"105"});
            });
        }else
            res.status(503).json({result:false,code:"Error Review del",erno:"105"});
    
    });
                }else
                    res.status(503).json({result:false,code:"Error Review del",erno:"105"});
        }else{
            res.status(503).json({result:false,code:"Error Review del",erno:"105"});
        }
    });   
});

router.get('/list', function(req,res){
    connection.query('select * from Review order by Review_date desc;' , function(error, rows){
        res.render('review_list',{rows: rows},function(error, content){
        if(!error){
            res.end(content);
        }else
        res.status(503).json({code:"Error Review list",erno:"107"});
    });
    });
});

router.get('/list/:Facility_id', function(req,res){
      console.log(req.params); 
    
    connection.query( 'select Review_title, Review_content, Review_photo, Member_emails, Facility_ids, Review_date, Review_score, Member_photo, Member_nick from Review,Member where Member_emails=Member_email and Facility_ids = ? order by Review_date desc;' ,[req.params.Facility_id], function(error, cursor){
        if(cursor.length>0){
            res.json(cursor);
        }else
        res.status(503).json({result : false,code:"No Review",erno:"107"});
    });
});


router.get('/rinfo/:id', function(req, res){
    
    connection.query('select Review_title,Review_content,Review_photo,Member_emails,Facility_ids,Review_date,Member_photo,Review_score, Member_nick from Review, Member where Member.Member_email = Review.Member_emails and Review.Review_id = ?;',[req.params.id], function(error, rows){
        if(error == null){
                res.json(rows);
            }else
                res.status(503).json({code:"Can't See the Review detail",erno:"108"});
        
        
    });
});

router.post('/rupdate',function (req, res){ //고치자
    connection.query('select * from Review where Review_id = ?;',[req.body.Review_id],
                    function(error, rows){
        if(error == null){
            if(rows[0].Member_emails == req.body.Member_emails){
                connection.query('update Review set Review_title =?, Review_content =?, Review_photo =? where Review_id =?;',[req.body.Reivew_title, req.body.Review_content, req.body.Review_photo, req.body.Review_id],function(error){
                    if(!error){
                        res.status(200).json({result : true});
                    }else
                        res.status(503).json({result : false});
                });
            }else
                res.status(503).json({result : false});
        }else
            res.status(503).json({result : false,code:"No Review up",erno:"107"});
    });
    
//    connection.query('update Review set Review_title =?, Review_content =? where Review_id =?;',[req.body.Review_title, req.body.Review_content, req.body.Review_id],function(error){
//        if(!error){
//            res.status(200).json({result: true});  
//        }else
//            res.status(503).json({result : false,code:"Review Update Fail",erno:"108"});
//    });
    
  //  connetion.query('select')
});


router.get('/delete/:id', function(req, res, next) {
    connection.query('delete from Review where Review_id = ?;', [req.params.id], function(error){
        if(!error)
            res.redirect('/review/list');

        else
            res.status(503);

    });
});

router.get('/update/:id', function(req, res, next) {
    connection.query('select * from Review where Review_id=?;', [req.params.id], function (error, cursor){
        if (cursor.length > 0) { 
                res.render('review_edit', {cursor : cursor});
            }
        else
            res.status(503).json({ result : false, reason : "Cannot post article"});
    });
});

router.post('/update_complete/:id', function (req, res) {
    connection.query('update Review set Review_title=?, Review_content=? where Review_id=?;',[ req.body.title, req.body.content, req.params.id],
                    function(error){
        if(!error){
            res.redirect('/review/list');
        }
        if(error){
            res.status(503);
        }
    });
});
            

   
module.exports=router;

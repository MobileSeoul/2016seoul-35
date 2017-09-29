var express = require('express');
var mysql = require('mysql');
var fs = require('fs');
var bodyParser = require('body-parser');
var async = require('async');
var multer = require('multer');
var router = express.Router();

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});


router.get('/', function(req, res, next) {
    // 그냥 board/ 로 접속할 경우 전체 목록 표시로 리다이렉팅
    res.redirect('/facil/up');
});

router.get('/del/', function(req, res) {
    connection.query('select * from Facility;',function(error, rows){
       res.render('facil_list',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});

router.get('/update/:id', function(req, res) {
    connection.query('select * from Facility where Facility_id = ?;',[req.params.id],function(error, rows){
       res.render('facil_edit',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});

router.post('/update_complete/:id', function (req, res) {
    connection.query('update Facility set Facility_name=?, Facility_addr=? where Facility_id=?;',[ req.body.name, req.body.addr, req.params.id ],
                    function(error){
        if(!error){
            res.redirect('/facil');
        }
        console.log("123");
        if(error){
            res.status(503);
        }
    });
});


router.get('/up/', function(req, res) {
    connection.query('select * from Facility;',function(error, rows){
       res.render('up_facil',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});

var _storage = multer.diskStorage({
    destination: function(req, file, cb){   //디렉토리 위치   
        cb(null, '/var/www/aws/bodeum/public/images');
        
    },
    filename: function(req, file, cb){  //파일명
        cb(null, Date.now() + "." + file.originalname.split('.').pop());
    }
});
var upload = multer({ storage: _storage});

router.get("/create",function (req, res) {
    res.render('facil_create', function (error, content) {
        if (!error) {
            res.end(content);
        }
        else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end("Error while reading a file");
        }
    });
});

router.post('/upload' ,upload.single('userPhoto'),function (req, res) {
    var filename = req.file.filename;
    console.log(filename);
    var path = req.file.path;
    
    connection.query('INSERT INTO Facility ( Facility_name,Facility_count,Facility_addr, Facility_call, Facility_avg, Facility_env,Facility_mang,Facility_sour,Facility_pro,Facility_chil,Facility_photo,Facility_aut,Facility_ecount,Facility_kind) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?) ;', [ req.body.name, req.body.count,req.body.addr,req.body.call,req.body.avg,req.body.env,req.body.mang,req.body.sour,req.body.pro,req.body.chil,filename,req.body.aut,req.body.ecount,req.body.kind,req.body.env], function (error, info) {

        if (!error)
//                res.sendStatus(503);
          ; //     res.redirect('/facil');
        else
             console.log("ddd");
        });
    });


router.post('/:id', function (req, res) {
    connection.query('update Facility set Facility_name=?, Facility_addr=? where Facility_id=?;',[ req.body.name, req.body.addr, req.params.id ],
                    function(error){
        if(!error){
            res.redirect('/facil');
        }
        console.log("123");
        if(error){
            res.status(503);
        }
    });
});

router.get('/totallist/:id', function(req, res){
     connection.query('select Facility_id, Facility_name,Facility_count,Facility_addr,Facility_call,Facility_avg,Facility_env,Facility_mang,Facility_sour,Facility_pro,Facility_chil,Facility_photo,Facility_aut,Facility_ecount,Facility_reviewnum,Facility_kind,Facility_score,(select Facility from Favorite where Favorite.Facility=Facility.Facility_id and Favorite.Member=?) as Favorite_facil from Facility ;', [req.params.id],function(error, cursor){        
if(cursor.length>0){
            res.json(cursor);
        }else
        res.status(503).json({code:"Error Reply list",erno:"117"});
    });
    
});

router.get('/list/:id/:email', function(req, res){
    async.parallel({
        facil : function(callback){
            connection.query('select Facility_id,Facility_name,Facility_count,Facility_addr,Facility_call,Facility_avg,Facility_env,Facility_mang,Facility_sour,Facility_pro,Facility_chil,Facility_photo,Facility_aut,Facility_ecount,Facility_reviewnum,Facility_kind,Facility_score,(select Facility from Favorite where Favorite.Facility=Facility.Facility_id and Favorite.Member=?) as Favorite_facil from Facility where Facility_id =?;',[req.params.email,req.params.id], callback)},        
review : function(callback){
            connection.query('select * from Review where Facility_ids =? ORDER BY Review_date DESC limit 1;',[req.params.id], callback)}
        },function(error, results){
        var facility = {};
        facility.facil =results.facil;
        facility.review = results.review;
        res.status(200).end(JSON.stringify(facility));
    
        
  
    });
    
});


//시설 검색
router.post('/search/where', function(req, res){
	console.log("is here??");
	var city=req.query.city;
	var dong=req.query.dong;
    city = '%'+city+'%';
	dong = '%'+dong+'%';
    
    connection.query('select * from Facility where Facility_kind like ?;',[dong], function(error, cursor){	
        console.log(cursor);
        res.json(cursor);
    });
});


//리뷰 순  리스트뽑기
router.get('/list_review', function(req, res){
	connection.query('select * from Facility order by Facility_reviewnum desc;',function(error, cursor){
		console.log(cursor);
		res.json(cursor);
	});
});
module.exports=router;

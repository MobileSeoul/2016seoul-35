var express = require('express');
var mysql = require('mysql');
var fs = require('fs');
var bodyParser = require('body-parser');
var router = express.Router();
var app = express();

var connection = mysql.createConnection({
    'host':'aws-db.c6m3cvpt4wbi.ap-northeast-2.rds.amazonaws.com',
    'user':'user',
    'password':'12345678',
    'database':'AWS-Board',
});
app.get('/',function(req,res){
    fs.readFile('index.ejs',function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});


/*router.get('/imageup/',function(req,res){
    connection.query('select * from Facility where Facility_id = 6;',function(error, rows){
        res.render('img',{rows: rows}, function(error, content){
            if(!error){
                res.end(content);
            }else{
        res.writeHead(501,{'Content-Type': 'text/plain'});
        res.end("Error");
            }
    });
    });
});*/

app.get('/imageup/:id',function(req,res){
    connection.query('select * from Facility where Facility_id = ?;',[req.params.id],function(error, rows){
        res.render('C:/Users/Administrator/Desktop/sopt/bodeum/views/img.ejs',{rows: rows}, function(error, content){
            if(!error){
                res.end(content);
            }else{
        res.writeHead(501,{'Content-Type': 'text/plain'});
        res.end('Error');
            }
    });
    });
});


/*router.get('/imgs',function(req,res){
    
    fs.readFile('C:/Users/Administrator/Desktop/sopt/bodeum/img/11.jpg',function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});*/
/*
app.get('/imgs',function(req,res){
    
    fs.readFile('C:/Users/Administrator/Desktop/sopt/bodeum/img/11.jpg',function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});

app.get('/imgs/:pt',function(req,res){
    var pt = req.params.pt;
    fs.readFile(pt,function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});

app.listen(3303,function(){
    console.log('Server Start.');
});*/

router.get('/', function(req, res, next) {
    // 그냥 board/ 로 접속할 경우 전체 목록 표시로 리다이렉팅
    res.redirect('/mboard/mup');
});

router.get('/list/', function(req, res) {
    connection.query('select * from Member;',function(error, rows){
       res.render('list',{rows: rows}, function(error, content) {
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
    connection.query('select * from Member where Member_email = ?;',[req.params.id],function(error, rows){
       res.render('edit',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});

router.post('/mup12/:id', function (req, res) {
    connection.query('update Member set Member_pass=?, Member_phone=?, Member_nick=? where Member_email=?;',[ req.body.pass, req.body.phone, req.body.nick, req.params.id ],
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
/*router.get('/mup12/:id', function (req, res) {
    connection.query('update Member set Member_pass=?, Member_phone=?, Member_nick=? where Member_id=?;',[ req.params.pass, req.params.phone, req.body.nick, req.params.id ],
                    function(error){
        if(!error){
            res.redirect('/mboard');
        }
        console.log("123");
        if(error){
            res.status(503);
        }
    });
});*/

router.get('/mup/', function(req, res) {
    connection.query('select * from Member;',function(error, rows){
       res.render('mup',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});

/*router.get('/mup/', function(req, res) {
    connection.query('select * from Member;',function(error, rows){
       res.render('mup',{rows: rows}, function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});
});*/


/*router.get('/',function(req, res){
   fs.readFile('index.ejs', 'utf8', function (error, data) {
        if (!error) {
            connection.query('select * from Member;', function (error, query) {
                
                res.writeHead(200, { 'Content-Type' : 'text/html; charset=utf-8;' });
                res.end(ejs.render(data, { 'query' : query }));
            });
        }
        else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end("Error while reading a file");
        }
    });
});*/
/*router.post('/delete:id', function(request, response, next){
    connection.query('delete from Member where Member_id = ?;', 
                    [request.body.id ],
                     function(error){
        if(!error){
            
            response.redirect('/mboard/list');
        }
        console.log(request.body);
        if(error){
            response.status(503);
        }
    });
});*/

router.get('/imageup',function(req,res){
    fs.readFile('../views/img.ejs',function(error,data){
        res.writeHead(200,{'Content-Type': 'text/html'});
        res.end(data);
    });
});
router.get('/imgs',function(req,res){
    fs.readFile('../img/11.jpg',function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});



module.exports=router;
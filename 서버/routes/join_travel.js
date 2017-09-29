var express = require('express');
var router = express.Router();
var mysql = require('mysql');

// rds
var connection = mysql.createConnection({
  'host' : 'aws-rds-mysql.cjj4qeglpmie.us-west-2.rds.amazonaws.com',
  'user' : 'user',
  'password' : 'rlawlsrb1!',
  'database' : 'TravelPlanner',
});

/*
 * Method       : POST
 * Path           : http://52.34.206.80:3000/join
 * Description  : 회원가입을 합니다.
 */
// 로컬에서 table명 member로 실험함, rds에서 table명은 Member
router.post('/', function(req, res, next) { 
  // email을 DB와 비교하여 이미 존재하면 회원가입이 되어있다고 알림, 존재하지 않는다면 새로 등록합니다.
  connection.query('select * from Member where email=? or id=?;', [req.body.email, req.body.id], function (error, cursor) {
    if (error == null) {
      if (cursor.length == 0) {
        connection.query('insert into Member (id, pw, email) values (?,?,?);', 
                         [req.body.id, req.body.pw, req.body.email], function (error, info) {
          res.status(200).json({ result : true });
        });
      } else {
        if (cursor[0].email == req.body.email) {
          // DB에 이미 존재하는 email (이미 회원가입 되어있음)
          res.status(406).json({ result : false, message : 'This email already exist' });
        }
        
        if (cursor[0].id == req.body.id) {
          // DB에 이미 존재하는 id (이미 회원가입 되어있음)
          res.status(405).json({ result : false, message : 'This id already exist' });
        }
        
      }
    } else {
      // debug
      // console.log('server DB load error');
      // res.status(503).json({ result : false, message : 'server DB error' })
    }
  });
});

module.exports = router;
var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res) {
  res.send('respond with a resource');
  //res.send(200, req.body.address);
});

module.exports = router;

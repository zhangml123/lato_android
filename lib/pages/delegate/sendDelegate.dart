import 'package:flutter/material.dart';

import 'package:fluttertoast/fluttertoast.dart';
import 'package:flutter/services.dart';
import 'dart:convert' as convert;

import 'package:platon_fans/generated/l10n.dart';
class SendDelegateView extends StatefulWidget {
  String nodeName;
  String nodeId;
  String url;
  SendDelegateView(this.nodeName, this.nodeId, this.url):super();

  @override
  SendDelegateViewState createState() {
    return new SendDelegateViewState();
  }
}

class SendDelegateViewState extends State<SendDelegateView> with SingleTickerProviderStateMixin {
  String _nodeName = "";
  String _nodeId = "";
  String _url = "";
  String _address = "";
  String _name = "";
  bool showValueError = false;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    _nodeName = widget.nodeName;
    _nodeId = widget.nodeId;
    _url = widget.url;
    sendMessage();
    receiveMessage();
  }




  Future<void> sendMessage() async {
    Map msg1 = new Map();
    msg1["method"] = "get_wallet_address";
    String reply1 = (await messageChannel.send(msg1)) as String;
    print("asset sendMessage get_wallet_address reply12222 = "+ reply1);
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    var method =  rs1["method"];
    print("asset sendMessage get_wallet_address reply44444 = "+ reply1);
    if(method == "get_wallet_address"){
    var address =  rs1["address"];
    var name =  rs1["name"];
    print("asset sendMessage get_wallet_address address = "+ address);
    this.setState(() {
      _address = address;
      _name = name;
    });
    }
  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("ValidatorDetailView detail22211111111  send_delegate = " + result.toString());

      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      print("ValidatorDetailView detail3333333  send_delegate ");
      var method = rs["method"];

      print("ValidatorDetailView detail4444443  send_delegate ");
      if(method == "send_delegate"){

        print("ValidatorDetailView detai5555555  send_delegate ");
        print("ValidatorDetailView detail  hash = " + rs["hash"]);
        if(rs["hash"] != null){

          print("ValidatorDetailView detail666666 send_delegate ");
          print("ValidatorDetailView detail  hash = " + rs["hash"]);
          Navigator.pop(context,"success");
        }
      }

    });
  }
  void simpleDialog(TextEditingController passwordController, TextEditingController valueController) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(S.of(context).inputPassword1,
              style: TextStyle(
                  fontFamily: "Sofia",
                  fontWeight: FontWeight.w700,
                  fontSize: 18.0)),
          content:TextField(
            controller: passwordController,
            obscureText: true,
            decoration: InputDecoration(
              labelText: S.of(context).password,
            ),
          ),
          actions: <Widget>[
            FlatButton(
              child:  Text(S.of(context).cancel , style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                valueController.text = "";
                Navigator.of(context).pop();
              },
            ),
            FlatButton(
              child:  Text(S.of(context).ok , style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                String password = passwordController.text;
                passwordController.text = "";
                String value = valueController.text;
                //valueController.text = "";
                checkPassword(password, value);
                Navigator.of(context).pop();
              },
            )
          ],
        );
      },
    );
  }
  Future<void> checkPassword(password, value) async {
    Map msg = new Map();
    msg["method"] = "check_password";
    msg["password"] = password;
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "check_password"){
      var status =  rs["status"];
      print("reply flutter checkPassword status  = "+ status);
      if(status == "success"){
        submit(password, value);
      }else{
        Fluttertoast.showToast(
            msg: "密码错误!",
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.CENTER,
            timeInSecForIosWeb: 1,
            backgroundColor: Colors.deepOrangeAccent,
            textColor: Colors.white,
            fontSize: 16.0
        );
      }

    }
  }
  Future<void> submit(password, value) async {
    if(showValueError) return;
    print("submit1111 = "+ _nodeId);
    print("submit1111 = "+ value);
    print("submit1111 = "+ password);
    Map msg = new Map();
    msg["method"]   = "send_delegate";
    msg["nodeId"]   = _nodeId;
    msg["value"]   = value;
    msg["password"]   = password;
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "send_delegate"){

    }
  }
  TextEditingController valueController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    valueController.addListener(() {
      print('input ${valueController.text}');
      if(double.parse(valueController.text) < 10 ){
        this.setState(() {
          showValueError = true;
        });
      }else{
        this.setState(() {
          showValueError = false;
        });
      }

    });
    passwordController.addListener(() {
      print('input ${passwordController.text}');

    });
    return Scaffold(

        resizeToAvoidBottomInset: false,
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "委托",
            style: TextStyle(
              fontFamily: "Sofia",

            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.chevron_left),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
        ),
        body:Column(
          children: [
            Padding(
            padding:
            const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
              child:Row(
                children: [
                  _url != "" ? Image.network(_url,width:60,height: 60,): Image(
                    width:60,
                    height:60,
                    image:  AssetImage("assets/images/main.png"),
                  ),
                Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _nodeName,
                      softWrap:false ,
                      overflow: TextOverflow.ellipsis,
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                    ),

                    Container(
                        width:MediaQuery.of(context).size.width-120,
                        child:Text(
                          _nodeId,
                          softWrap:false ,
                          overflow: TextOverflow.ellipsis,
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                        ),
                    ),
                  ]
                )
                ],
              )
            )
,
            Padding(
                padding:
                const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
                child:Row(
                  children: [
                    Image(
                        width:60,
                        height:60,
                        image:  AssetImage("assets/images/main.png")
                    ),
                    Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            _name,
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),
                          Container(
                              width:MediaQuery.of(context).size.width-120,
                              child:Text(
                            _address,
                            softWrap:false ,
                            overflow: TextOverflow.ellipsis,
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                          ),),
                        ]
                    )
                  ],
                )
            ),
            Padding(
                padding:
                const EdgeInsets.only(left: 20.0, right: 20.0, top: 8.0, bottom: 5.0),
                child:  TextField(
                  controller: valueController,
                  decoration:
                  InputDecoration(
                    labelText: "委托数量",
                    errorText: showValueError ? "委托数量不能小于10LAT":null,
                     // suffixText: "可用余额 "

                  ),
                ),
            ),

            SizedBox(height: 50,),
            ElevatedButton(
              style: ButtonStyle(
                textStyle: MaterialStateProperty.all(
                    TextStyle(fontSize: 18,)),
                padding: MaterialStateProperty.all(EdgeInsets.only(left:70,right: 70,top:5,bottom: 5)),
                shape: MaterialStateProperty.all(
                    StadiumBorder(
                        side: BorderSide(
                          color: Colors.white,
                        )
                    )
                )
                ,
                backgroundColor:MaterialStateProperty.all(Color(0xFFFFFFFF)),

              ),
              child: Text(
                "委托",
                style: TextStyle(color: Colors.blue),
              ),

              onPressed: () {
                simpleDialog(passwordController, valueController);
              },
            ),
            SizedBox(height: 50,),
            Padding(
                padding:
                const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
                child:Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(

                        children: [
                          Container(
                            width:4,
                            height:16,
                            decoration: BoxDecoration(
                              color: Colors.lightBlue,
                            ),
                          ),
                         SizedBox(
                           width: 10,
                         ),
                         Text(
                            "委托",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 16.0),
                          ),

                        ]
                    ),
                    Text(
                      "委托的LAT可以随时收回",
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                    ),
                    Text(
                      "验证节点关联的操作地址不允许参与委托。",
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 16.0),
                    ),

                  ],
                )
            ),
            Padding(
                padding:
                const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
                child:Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                        children: [
                          Container(
                            width:4,
                            height:16,
                            decoration: BoxDecoration(
                              color: Colors.lightBlue,
                            ),
                          ),
                          SizedBox(
                            width: 10,
                          ),
                          Text(
                            "收益",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 16.0),
                          ),

                        ]
                    ),
                    Text(
                      "委托至少锁定一个周期（10750 区块）才有收益。根据节点设置的委托奖励比例对节点获得的收益进行分配。节点收益包含出块奖励和质押奖励。",
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 16.0),
                    ),


                  ],
                )
            ),
          ])
    );
  }

}
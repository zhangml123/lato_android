import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:platon_fans/pages/asset/list.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/pages/delegate/sendDelegate.dart';
import 'package:platon_fans/pages/delegate/validator.dart';
import 'package:platon_fans/pages/delegate/withdrawDelegate.dart';
import 'package:platon_fans/pages/scan.dart';
import 'package:platon_fans/pages/walletSelect.dart';
import 'package:fluttertoast/fluttertoast.dart';

class DelegateView extends StatefulWidget {
  @override
  DelegateViewState createState() {
    return new DelegateViewState();
  }
}

class DelegateViewState extends State<DelegateView> with SingleTickerProviderStateMixin {
  String _address = "";
  String _name = "";
  List _verifyNodeList = [];
  List delegateDetailList = [];
  Map<String, dynamic> myDelegate = {};
  late TabController _tabController;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());

  TextEditingController valueController = TextEditingController();
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this,  length: 2);
    receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_verify_node_list";
    String reply = (await messageChannel.send(msg)) as String;

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
    Map msg2 = new Map();
    msg2["method"] = "get_my_delegate_list";
    String reply2 = (await messageChannel.send(msg2)) as String;

    Map msg3 = new Map();
    msg3["method"] = "get_delegate_detail_list";
    String reply3 = (await messageChannel.send(msg3)) as String;

  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("asset detail  receiveMessage = " + result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method = rs["method"];
      if(method == "get_verify_node_list"){
        this.setState(() {
          print("get_verify_node_list222222222");

          print(rs["node_list"]);
          _verifyNodeList = rs["node_list"];
        });
      }

      if(method == "get_my_delegate_list"){
        this.setState(() {

          print("get_my_delegate_list");
          print(rs["delegate_list"]);
          myDelegate = rs["delegate_list"][0];
        });
      }

      if(method == "get_delegate_detail_list"){
        this.setState(() {

          print("get_delegate_detail_list");
          print(rs);
this.setState(() {
  delegateDetailList =rs["delegate_list"];
});
        });
      }

    });
  }
  Future<void> withdrawDelegate(String password, String nodeId, String amount, String stakingBlockNum) async {


      Map msg = new Map();
      msg["method"] = "withdraw_delegate";
      msg["amount"] = amount;
      msg["nodeId"] = nodeId;
      msg["password"] = password;
      msg["stakingBlockNum"] = stakingBlockNum;
      String reply = (await messageChannel.send(msg)) as String;



  }
  Future<void> checkPassword(String password, String nodeId, String amount, String stakingBlockNum) async {
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
        withdrawDelegate(password, nodeId, amount, stakingBlockNum);
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
  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  String format(String num){

    //final a  =  BigInt.parse(num);
   // final b  =  BigInt.parse("1000000000000000000");
return num;
    //return (a / b).toString();
  }
  @override
  Widget build(BuildContext context) {
    valueController.addListener(() {
      print('input ${valueController.text}');
    });
    return Scaffold(
      backgroundColor: Colors.white,
        body:
          Container(
            decoration: BoxDecoration(
              image:  DecorationImage(image: AssetImage("assets/images/background.png"), fit: BoxFit.fitWidth,alignment: Alignment.topCenter),

            ),
            child: Column(
              children: [
                Container(
                    height: 60.0,
                    width: MediaQuery.of(context).size.width,
                    child:
                    Padding(
                      padding: const EdgeInsets.only(left: 10.0, right: 10.0, top: 25),
                      child:TabBar(
                        indicatorColor: Colors.white,
                        indicatorSize: TabBarIndicatorSize.tab,
                        indicatorWeight: 1,
                        isScrollable:false,
                        tabs: [
                          Tab(icon: Text("我的委托",style: TextStyle(fontSize: 20),)),
                          Tab(icon: Text("验证节点",style: TextStyle(fontSize: 20),))
                        ],
                        controller: _tabController,
                      ),
                    )
                ),

                Container(
                  height: MediaQuery.of(context).size.height -60,
                  child: TabBarView(
                    controller: _tabController,
                    children: [
                      SingleChildScrollView(
                        child: Column(
                          children: [
                            Padding(
                              padding:
                              const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 0.0),
                              child: Container(
                                height: 250.0,
                                width: 400.0,
                                child: DecoratedBox(
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                    boxShadow: [
                                      BoxShadow(
                                        color: Color(0xFF111111).withOpacity(0.3),
                                        blurRadius: 3.0,
                                        spreadRadius: 3.0,
                                      ),
                                    ],

                                  ),
                                  child: Padding(
                                    padding:
                                    const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 0.0),
                                    child: Column(
                                      children: <Widget>[
                                        Row(
                                          children: <Widget>[
                                            Container(
                                              height: 40.0,
                                              width: 40.0,
                                              decoration: BoxDecoration(
                                                color: Colors.white,
                                                //borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                                image:
                                                DecorationImage(image: AssetImage("assets/images/main.png"), fit: BoxFit.fill),

                                              ),
                                            ),
SizedBox(width: 10,),
                                            Column(
                                              crossAxisAlignment: CrossAxisAlignment.start,
                                                children: <Widget>[
                                                  Container(
                                                    height: 25.0,
                                                    width: 220.0,
                                                    child: Text(
                                                      _name,
                                                      style:
                                                      TextStyle(fontFamily: "Sofia", color: Colors.black87,fontWeight:  FontWeight.w800, fontSize: 18.0),
                                                    ),
                                                  ),
                                                  Container(
                                                    height: 30.0,
                                                    width: 250.0,
                                                    child: Text(
                                                      _address,
                                                      softWrap:false ,
                                                      overflow: TextOverflow.ellipsis,
                                                      style:
                                                      TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                                                    ),

                                                  ),
                                                ]),

                                          ],
                                        ),

                                        Container(
                                          height: 1.0,
                                          width: MediaQuery.of(context).size.width,
                                          decoration: BoxDecoration(
                                            color: Colors.black38,
                                          ),
                                        ),
                                        Padding(
                                          padding:
                                          const EdgeInsets.only(left: 20.0, right: 20.0, top: 10.0, bottom: 10.0),
                                          child:
                                              Row(
                                                mainAxisAlignment: MainAxisAlignment.center,
                                                children: <Widget>[
                                                  Column(
                                                      children: <Widget>[
                                                        Container(
                                                          height: 30.0,

                                                          child: Text(
                                                            "总计委托 （LAT）",
                                                            style:
                                                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                          ),
                                                        ),
                                                        Container(
                                                          height: 30.0,
                                                          child: Text(
                                                            myDelegate["delegated"] != null ? format(myDelegate["delegated"]) : "0.00",
                                                            style:
                                                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                                                          ),

                                                        ),
                                                      ]),


                                                ],
                                              ),

                                        ),
                                        Padding(
                                          padding:
                                          const EdgeInsets.only(left: 20.0, right: 20.0, top: 0.0, bottom: 10.0),
                                          child:
                                          Row(
                                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                            children: <Widget>[
                                              Column(
                                                  children: <Widget>[
                                                    Container(
                                                      height: 30.0,

                                                      child: Text(
                                                        "待领取奖励",
                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                      ),
                                                    ),
                                                    Container(
                                                      height: 30.0,
                                                      child: Text(
                                                          myDelegate["withdrawReward"] != null ? format(myDelegate["withdrawReward"]):"0.00",

                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                                                      ),

                                                    ),
                                                  ]),
                                              Column(
                                                  children: <Widget>[
                                                    Container(
                                                      height: 30.0,
                                                      child: Text(
                                                        "累计奖励",
                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                      ),
                                                    ),
                                                    Container(
                                                      height: 30.0,
                                                      child: Text(
                                                        myDelegate["cumulativeReward"] != null ?myDelegate["cumulativeReward"]:"0.00",
                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                                                      ),

                                                    ),
                                                  ]),


                                            ],
                                          ),

                                        ),
                                        Container(
                                          height: 1.0,
                                          width: MediaQuery.of(context).size.width,
                                          decoration: BoxDecoration(
                                            color: Colors.black38,
                                          ),
                                        ),
                                        /*Padding(
                                          padding:
                                          const EdgeInsets.only(left: 20.0, right: 20.0, top: 10.0, bottom: 10.0),
                                          child:
                                          Row(
                                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                            children: <Widget>[
                                              Column(
                                                  children: <Widget>[
                                                    Container(
                                                      height: 30.0,
                                                      child: Text(
                                                        "委托记录",
                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.blue, fontSize: 18.0),
                                                      ),

                                                    ),
                                                  ]),

                                              Column(
                                                  children: <Widget>[

                                                    Container(
                                                      height: 30.0,

                                                      child: Text(
                                                        "领取记录",
                                                        style:
                                                        TextStyle(fontFamily: "Sofia", color: Colors.blue, fontSize: 18.0),
                                                      ),

                                                    ),
                                                  ]),

                                            ],
                                          ),

                                        ),*/
                                            ],
                                          ),

                                        ),



                                ),

                              ),
                            ),
                            Container(
                              height: 500,
                              child: ListView.builder(
                              //controller: _controller,
                              itemCount: delegateDetailList.length,
                              itemBuilder: (BuildContext context, int position) {
                                Map<String, dynamic> detail =  delegateDetailList[position];
                                print("detail1111=");
                                print(detail);
                                return  _list(position,
                                    Icons.gamepad, detail["nodeName"],detail["delegated"],detail["withdrawReward"], detail["url"],detail["nodeStatus"], detail['nodeId']);
                              },
                            ),
                            )

                          ],



                        ),
                      ),
                      new ValidatorView(_verifyNodeList),
                    ],

                  ),
                ),

              ],
            ),
        )


    );

  }
  Widget _list(int position, IconData icon, String name,  String delegated, String withdrawReward, String url, String nodeStatus, String nodeId) {
    return Padding(
      padding: const EdgeInsets.only(left: 15.0, right: 15.0, bottom: 0.0),
      child:
      Container(
          height: 200.0,
          decoration: BoxDecoration(
            gradient: LinearGradient(colors: [ Color(0xFF66ffee),Color(0xFFffffff)], begin: FractionalOffset(1, 0), end: FractionalOffset(0, 1)),
            //color: Colors.blueAccent,
            borderRadius: BorderRadius.all(Radius.circular(15.0)),
            boxShadow: [
              BoxShadow(
                color: Color(0xFF000000).withOpacity(0.1),
                blurRadius: 3.0,
                spreadRadius: 3.0,
              ),
            ],
          ),
          child:
          Padding(
            padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 15.0),
            child:
            InkWell(
              onTap: () {
                // Navigator.of(context).push(PageRouteBuilder(
                //    pageBuilder: (_, __, ___) => new AssetDetailView()));

              },
              child:Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Container(
                            height: 40.0,
                            width: 40.0,
                            child: Center(
                              child: url != "" ? Image.network(url): Image(
                                width:60,
                                height:60,
                                image:  AssetImage("assets/images/main.png") ,
                              ),
                            ),
                          ),
                          SizedBox(
                            width: 12.0,
                          ),
                          Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(name,
                                style: TextStyle(
                                fontFamily: "Popins",
                                fontWeight: FontWeight.w600,
                                fontSize: 15.5,
                              ),),
                              Container(
                                width:MediaQuery.of(context).size.width-220,
                                child: Text(nodeId,
                                  softWrap:false ,
                                  overflow: TextOverflow.ellipsis,
                                  style: TextStyle(
                                      fontSize: 16
                                  ),
                                ),
                              ),
                              /*Text("已委托 : "+ delegated),
                              Text("可领取奖励 : "+withdrawReward)
*/
                            ],
                          )
                        ],
                      ),
                      Column(
                          children: <Widget>[
                            Text(
                              nodeStatus == "Active" ? "共识中":"",
                              style: TextStyle(
                                fontFamily: "Popins",
                                color: Colors.blueAccent,
                                fontWeight: FontWeight.w800,
                                fontSize: 16.5,
                              ),
                            ),
                            //isSetting && position != 0?

                          ])

                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                        children: <Widget>[

                          Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text("已委托",
                                style: TextStyle(
                                  fontFamily: "Popins",
                                  fontSize: 15.5,
                                ),),
                              Text(delegated,
                                  softWrap:false ,
                                  overflow: TextOverflow.ellipsis,
                                  style: TextStyle(
                                      fontSize: 16
                                  ),
                                ),

                              /*Text("已委托 : "+ delegated),
                              Text("可领取奖励 : "+withdrawReward)
*/
                            ],
                          )
                        ],
                      ),
                      Row(
                        children: <Widget>[

                          Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text("待赎回委托",
                                style: TextStyle(
                                  fontFamily: "Popins",
                                  fontSize: 15.5,
                                ),),
                              Text("",
                                softWrap:false ,
                                overflow: TextOverflow.ellipsis,
                                style: TextStyle(
                                    fontSize: 16
                                ),
                              ),

                              /*Text("已委托 : "+ delegated),
                              Text("可领取奖励 : "+withdrawReward)
*/
                            ],
                          )
                        ],
                      )
                    ],
                  ),
                  SizedBox(height: 10,),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                          Text("可领取奖励： "+withdrawReward),
                          InkWell(
                            onTap: () {


                              simpleDialog(withdrawReward, nodeId);
                            },
                            child:
                            Text("领取奖励",style: TextStyle(
                                fontSize: 16,
                                color: Colors.lightBlue
                            ),),
                          ),


                    ],
                  ),
                  SizedBox(height: 15,),
                  Container(
                      height: 2.0,
                      decoration: BoxDecoration(
                        color:Colors.white
                      )
                  ),
                  Padding(padding:  const EdgeInsets.only(left: 30.0, right: 30.0, top: 15.0),
                  child:Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      InkWell(
                        onTap: () {
                          // Navigator.of(context).push(PageRouteBuilder(
                          //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                          Navigator.push(
                            context,
                            MaterialPageRoute(builder: (context) {
                              return SendDelegateView(name, nodeId, url);

                            }),
                          ).then((data){
                            if(data == "success"){

                              //Navigator.of(context).pop();
                            }
                          });
                        },
                        child:
                        Text("委托",style: TextStyle(
                            fontSize: 16,
                            color: Colors.lightBlue
                        ),),
                      ),
                      InkWell(
                        onTap: () {
                          // Navigator.of(context).push(PageRouteBuilder(
                          //    pageBuilder: (_, __, ___) => new AssetDetailView()));

                        },
                        child:
                        Text("赎回委托",style: TextStyle(
                            fontSize: 16,
                            color: Colors.lightBlue,
                          fontWeight: FontWeight.w800
                        ),),
                      ),

                    ],
                  ),
                  ),

                ],
              ),
            ),
          )
      ),
    );
  }
  void simpleDialog( String withdrawReward, String nodeId ) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text("领取奖励",
              style: TextStyle(
                  fontFamily: "Sofia",
                  fontWeight: FontWeight.w700,
                  fontSize: 18.0)),
          content:

          Container(
            height: 200,
            width: 1300,
            child:Column(
              children:[
                Text(withdrawReward+" LAT"),

                Text("预估奖励，以实际到账金额为准，一次最多可领取20个节点的奖励。"),

                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    //Text("交易手续费"),
                    //Text("0.001 LAT")

                  ],
                ),
                SizedBox(height: 20,),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("领取钱包"),
                    Text(_name)

                  ],
                ),
                SizedBox(height: 20,),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("钱包密码:"),
                    Container(width: 200,height: 40,child: TextField(
                      controller: valueController,

                      obscureText: true,
                    ),),
                  ],
                ),
                ],
            ),
          ),
          actions: <Widget>[
            FlatButton(
              child: const Text('取消', style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                valueController.text = "";
                Navigator.of(context).pop();
              },
            ),
            FlatButton(
              child: const Text('确定', style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {

                checkPassword(valueController.text, nodeId, withdrawReward, "");
                //Navigator.of(context).pop();
              },
            )
          ],
        );
      },
    );
  }

}
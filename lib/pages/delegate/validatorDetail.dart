import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/pages/delegate/sendDelegate.dart';
class ValidatorDetailView extends StatefulWidget {
  String nodeId;
  ValidatorDetailView(this.nodeId):super();
  @override
  ValidatorDetailViewState createState() {
    return new ValidatorDetailViewState();
  }
}

class ValidatorDetailViewState extends State<ValidatorDetailView> with SingleTickerProviderStateMixin {
  late String _nodeId;
  List validators = [];
  late TabController _tabController;
  Map<String, dynamic> _nodeDetail = {};
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: 2);
    _nodeId = widget.nodeId;
    receiveMessage();
    sendMessage();
  }
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());


  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_verify_node_detail";

    msg["nodeId"] = _nodeId;
    String reply = (await messageChannel.send(msg)) as String;
  }

  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("ValidatorDetailView detail  receiveMessage = " + result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method = rs["method"];
      if(method == "get_verify_node_detail"){
        this.setState(() {
          print("get_verify_node_detail");
          print(rs["node_detail"]);
          _nodeDetail = rs["node_detail"];

        });
      }

    });
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor:Color(0xFFFAFAFA),
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "节点详情",
            style: TextStyle(
                fontFamily: "Sofia",
                color: Colors.white
            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.chevron_left),
            color: Colors.white,
            onPressed: () {
              Navigator.pop(context,"detail");
            },
          ),
        ),
        body:
      Column(
        children: [
          Padding(
            padding:
            const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
            child: Container(
              height: 300.0,
              width: 400.0,
              child: DecoratedBox(
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.all(Radius.circular(15.0)),
                  boxShadow: [
                    BoxShadow(
                      color: Color(0xFFAAAAAA).withOpacity(0.4),
                      blurRadius: 5.0,
                      spreadRadius: 5.0,
                    ),
                  ],

                ),
                child: Padding(
                  padding:
                  const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 00.0),
                  child: Column(
                    children: <Widget>[
                      Padding(
                        padding:
                        const EdgeInsets.only(left: 20.0, right: 20.0, top: 0.0, bottom: 0.0),
                        child:
                            Column(
                                children: <Widget>[
                                  Container(
                                    alignment: Alignment.topRight,
                                    height: 20.0,
                                    child: Text(
                                      "共识中",
                                      style:
                                      TextStyle(fontFamily: "Popins",
                                        color: Colors.blueAccent,
                                        fontWeight: FontWeight.w800,
                                        fontSize: 16.5,),
                                    ),

                                  ),
                                  Container(
                                    height: 60.0,
                                    child: _nodeDetail["url"] != null && _nodeDetail["url"] != "" ? Image.network(_nodeDetail["url"]): Image(
                                      width:60,
                                      height:60,
                                      image:  AssetImage("assets/images/main.png") ,
                                    ),

                                  ),
                                  Container(
                                    height: 30.0,
                                    child: Text(

                                      _nodeDetail["name"] !=null ? _nodeDetail["name"]:"",
                                      style:
                                      TextStyle(fontFamily: "Sofia", color: Colors.black54,fontWeight: FontWeight.w800, fontSize: 18.0),
                                    ),

                                  ),
                                  Container(
                                    height: 30.0,
                                    child: Text(
                                      _nodeDetail["nodeId"] !=null ? _nodeDetail["nodeId"]:"",
                                      softWrap:false ,
                                      overflow: TextOverflow.ellipsis,
                                      style:
                                      TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                                    ),

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
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: <Widget>[
                                        Column(
                                            children: <Widget>[
                                              Container(
                                                height: 30.0,

                                                child: Text(
                                                  "预计委托收益率",
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                ),
                                              ),
                                              Container(
                                                height: 30.0,
                                                child: Text(
                                                   _nodeDetail["delegatedRatePA"] != null ? _nodeDetail["delegatedRatePA"] : "",
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                                                ),

                                              ),
                                            ]),

                                        Column(
                                            children: <Widget>[
                                              Container(
                                                height: 30.0,
                                                child: Text(
                                                  "委托奖励比例",
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                ),
                                              ),
                                              Container(
                                                height: 30.0,

                                                child: Text(
                                                  _nodeDetail["delegatedRewardPer"] !=null ?_nodeDetail["delegatedRewardPer"] : "",
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
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
                                    SizedBox(height: 15,),
                                    Container(
                                      alignment: Alignment.topLeft,
                                      height: 30.0,
                                      child: Text(
                                        _nodeDetail["cumulativeReward"] != null ? ("累计奖励："+ _nodeDetail["cumulativeReward"] + " LAT" ) : "",
                                        style:
                                        TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 14.0),
                                      ),

                                    ),

                                ]),
                      ),
                    ],
                  ),

                ),

              ),

            ),
          ),
          SizedBox(height: 10.0),
          Padding(
          padding:
          const EdgeInsets.only(left: 20.0, right: 20.0, top: 10.0, bottom: 10.0),
          child:
              Column(
                children: [
                  Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "总质押(LAT)",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(
                            _nodeDetail["deposit"] !=null ?_nodeDetail["deposit"]:"0",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),
                  Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "总委托",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(
                              _nodeDetail["delegateSum"] !=null ?_nodeDetail["delegateSum"]:"0",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "委托者数",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(
                            _nodeDetail["delegate"] !=null ?_nodeDetail["delegate"]:"0",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "已出块数",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(

                            _nodeDetail["blockOutNumber"] !=null ?_nodeDetail["blockOutNumber"].toString():""
                            ,
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),
                  Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "出块率",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(
                              _nodeDetail["blockRate"] !=null ?_nodeDetail["blockRate"]:"0",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          child: Text(
                            "处罚次数",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                          ),
                        ),
                        Container(
                          height: 40.0,
                          child: Text(

                            _nodeDetail["punishNumber"] !=null ?_nodeDetail["punishNumber"].toString():"0",
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 18.0),
                          ),

                        ),
                      ]
                  ),
                  ElevatedButton(
                    style: ButtonStyle(
                      textStyle: MaterialStateProperty.all(
                          TextStyle(fontSize: 18,)),
                      padding: MaterialStateProperty.all(EdgeInsets.only(left:55,right: 55,top:5,bottom: 5)),
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

                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) {
                          return SendDelegateView(_nodeDetail["name"], _nodeDetail["nodeId"], _nodeDetail["url"]);

                        }),
                      ).then((data){
                        if(data == "success"){

                          //Navigator.of(context).pop();
                        }
                      });
                    },
                  ),
                ],
              ),



          ),

        ],
      )
      );

  }

}
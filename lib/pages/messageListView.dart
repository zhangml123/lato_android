import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'dart:convert' as convert;

import 'package:platon_fans/generated/l10n.dart';
class MessageListView extends StatefulWidget {
  MessageListViewState createState() => MessageListViewState();
}

class MessageListViewState extends State<MessageListView>{
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  List _messageList = [];
  void initState() {
    super.initState();
    //receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_message_list";
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive  = "+ reply);
    var method =  rs["method"];
    if(method == "get_message_list"){
      var messageList =  rs["message_list"];
      this.setState(() {
        _messageList = messageList;
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
              S.of(context).newAsset,
            style: TextStyle(
              fontFamily: "Sofia",
            ),
          ),
        ),
        body:
          ListView.builder(
            itemCount: _messageList.length,
            itemBuilder: (BuildContext context, int position) {
              Map<String, dynamic> message =  _messageList[position];
              String msg =  message["msg"];
              Map<String, dynamic> rs = convert.jsonDecode(msg);
              print(rs["from"]);
              return  _list(rs["from"], rs["contractAddress"], rs["showValue"]+ " "+rs["symbol"], "", message["id"]);

            },
          ),

    );
  }
  Widget _list(String from, String contractAddress, String value, String assetId, String messageId){
    return Padding(
        padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 20.0,),
    child:
      Container(
          height: 200.0,
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(15.0)),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                color: Color(0xFFABABAB).withOpacity(0.4),
                offset : Offset(3.0, 3.0),
                blurRadius: 5.0,
                spreadRadius: 5.0,
              ),
            ],
          ),
        child:Column(
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(S.of(context).fromAddress+": ",),
                Container(
                  width: MediaQuery.of(context).size.width -170,
                  height: 50,
                  child:Text(
                    from,
                    //softWrap:false ,
                    //overflow: TextOverflow.ellipsis,
                  )
                )

              ],
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(S.of(context).contractAddress+": ",),

                Container(
                    width: MediaQuery.of(context).size.width -170,
                    height: 50,
                    child:Text(
                      contractAddress,
                      //softWrap:false ,
                     // overflow: TextOverflow.ellipsis,
                    )
                )
              ],
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [

                Container(
                  width: (MediaQuery.of(context).size.width -50) /2,
                    child: Row(
                        children: [
                          Text(S.of(context).amount+": "),
                          Container(
                            width: (MediaQuery.of(context).size.width -50) /2 - 80,

                            child: Text(
                              value,
                              softWrap:false ,
                              overflow: TextOverflow.ellipsis,
                              ),
                          )

                        ])
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                ElevatedButton(
                  style: ButtonStyle(
                    textStyle: MaterialStateProperty.all(
                        TextStyle(fontSize: 14,)),
                    padding: MaterialStateProperty.all(EdgeInsets.only(left:15,right: 15)),
                    shape: MaterialStateProperty.all(
                        StadiumBorder(
                            side: BorderSide(
                              color: Colors.white,
                            )
                        )
                    ),
                    backgroundColor:MaterialStateProperty.all(Color(0xFFFFFFFF)),

                  ),
                  child: Text(
    S.of(context).add,
                    style: TextStyle(color: Colors.blue),
                  ),
                  onPressed: () {
                    print("messageId = "+ messageId);
                    (()async {
                      Map msg = new Map();
                          msg["method"] = "add_asset";
                          msg["contractAddress"] = contractAddress;
                          msg["assetId"] = assetId;
                          msg["messageId"] = messageId;
                          String reply = (await messageChannel.send(msg)) as String;
                      print("reply flutter receive  = "+ reply);
                      Map<String, dynamic> rs = convert.jsonDecode(reply);
                      if(rs["method"] == "add_asset" && rs["code"] == "1"){
                        print(rs["msg"]);
                        Navigator.of(context).pop();
                      }
                    })();


                  },
                ),
                SizedBox(width: 10,),
                /*ElevatedButton(
                  style: ButtonStyle(
                    textStyle: MaterialStateProperty.all(
                        TextStyle(fontSize: 14,)),
                    padding: MaterialStateProperty.all(EdgeInsets.only(left:15,right: 15)),
                    shape: MaterialStateProperty.all(
                        StadiumBorder(
                            side: BorderSide(
                              color: Colors.white,
                            )
                        )
                    ),
                    backgroundColor:MaterialStateProperty.all(Color(0xFFFFFFFF)),

                  ),
                  child: Text(
                    "删除",
                    style: TextStyle(color: Colors.red),
                  ),

                  onPressed: () {

                  },
                ),*/

              ],
            )
          ],
        )
      )
    );
  }
}
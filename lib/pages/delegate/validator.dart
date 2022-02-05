import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:platon_fans/pages/delegate/validatorDetail.dart';

class ValidatorView extends StatefulWidget {
  List verifyNodeList;
  ValidatorView(this.verifyNodeList):super();
  @override
  ValidatorViewState createState() {
    return new ValidatorViewState();
  }
}

class ValidatorViewState extends State<ValidatorView> with SingleTickerProviderStateMixin {
  List _verifyNodeList = [];
  late TabController _tabController;
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: 2);
    _verifyNodeList = widget.verifyNodeList;
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }
  @override
  void didUpdateWidget(ValidatorView oldWidget) {
    super.didUpdateWidget(oldWidget);
    if(oldWidget.verifyNodeList != widget.verifyNodeList){

      print("verifyNodeList11111 = " );

      print( widget.verifyNodeList);
      this.setState(() {
        _verifyNodeList = widget.verifyNodeList;

      });
    }


  }
  @override
  Widget build(BuildContext context) {
    return
       Column(
            children: [
              SizedBox(height: 10.0),
              Container(
                height: MediaQuery.of(context).size.height-100,
                width: MediaQuery.of(context).size.width,
                decoration: BoxDecoration(
                  color: Colors.white,
                ),
                child:
                ListView.builder(
                  //controller: _controller,
                  itemCount: _verifyNodeList.length,
                  itemBuilder: (BuildContext context, int position) {
                    Map<String, dynamic> node =  _verifyNodeList[position];

                    return  _list(position,
                        Icons.gamepad, node["name"], node["delegateSum"], node["showDelegatedRatePA"], node["url"], node["nodeId"]);
                  },
                ),
              ),
            ],

        );

  }
  Widget _list(int position, IconData icon, String name,  String delegateSum, String showDelegatedRatePA, String url, String nodeId) {
    return Padding(
      padding: const EdgeInsets.only(left: 15.0, right: 15.0, bottom: 10.0),
      child:
      Container(
          height: 80.0,
          decoration: BoxDecoration(
            gradient: LinearGradient(colors: [ Color(0xFF51FFCD),Color(0xFFffffff)], begin: FractionalOffset(1, 0), end: FractionalOffset(0, 1)),
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
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) {
                      return ValidatorDetailView(nodeId);
                    }),
                  );
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
                                Text(name,style: TextStyle(
                                  fontFamily: "Popins",
                                  fontWeight: FontWeight.w600,
                                  fontSize: 15.5,
                                ),),
                                Text("总质押 : "+ delegateSum),

                            ],
                          )
                        ],
                      ),
                      Column(
                          children: <Widget>[
                            Text(
                              "预计收益率",
                              style: TextStyle(
                                fontFamily: "Popins",
                                fontWeight: FontWeight.w700,
                                color: Colors.white,
                                fontSize: 15.5,
                              ),
                            ),
                            //isSetting && position != 0?
                            Text(
                              showDelegatedRatePA,
                              style: TextStyle(
                                fontFamily: "Popins",
                                fontWeight: FontWeight.w700,
                                color: Colors.white,
                                fontSize: 15.5,
                              ),
                            ),
                          ])

                    ],
                  ),
                ],
              ),
            ),
          )
      ),
    );
  }
}
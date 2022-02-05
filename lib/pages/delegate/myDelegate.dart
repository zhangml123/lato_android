
import 'package:flutter/material.dart';
class MyDelegateView extends StatefulWidget {
  @override
  MyDelegateViewState createState() {
    return new MyDelegateViewState();
  }
}

class MyDelegateViewState extends State<MyDelegateView> with SingleTickerProviderStateMixin {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        body:Text("asdf")
    );
  }

}

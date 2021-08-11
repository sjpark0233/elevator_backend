<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Elevator</title>
    </head>
    <body>
        <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
        <script>
            function doReservation() {
                var formData = {
                    name: $('#name').val(),
                    departureFloor: $('#departureFloor').val(),
                    destinationFloor: $('#destinationFloor').val(),
                    elevatorId: 1
                };

                $.ajax({
                    url :'/elevator/reservation',
                    type :'POST',
                    data : JSON.stringify(formData),
                    dataType : 'json',
                    contentType:'application/json; charset=utf-8',
                    success : function(data) {
                        alert("예약성공");
                        location.href='/';
                    },
                    error : function(xhr, status, error) {
                        alert("예약실패 : " +xhr + status + error);
                    }
                });
            }
        </script>
        <p>1. 가동</p>
            <button onclick="location='/elevator/operate'">가동</button>
        <p>2. 탑승예약</p>
            <form id="form_reservation" method="POST">
                <p>
                    이  름 : <input type="text" id="name" size="10"><br/>
                    현재층 : <input type="text" id="departureFloor" size="10"><br/>
                    목적층 : <input type="text" id="destinationFloor" size="10"><br/>
                </p>
            </form>
            <input type="button" value="확인" onclick="doReservation()">
        <p>3. 상태확인</p>
            <button onclick="location='/elevator/status'">상태확인</button>
        <p>4. 비상정지</p>
            <button onclick="location='/elevator/emergencyStop'">비상정지</button>
    </body>
</html

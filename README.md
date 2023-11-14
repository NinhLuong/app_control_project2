# App control
1. **Mục tiêu của đề tài thiết kế hệ thống với khả năng giám sát và tự động điều khiển hệ thống hoạt động trong quá trình chong đèn thanh long**
  + Hệ thống sẽ thu thập liên tục các dữ liệu môi trường, sau đó sẽ xử lý rồi đồng bộ lên Firebase.
  + Mọi thứ sẽ được hiện thị và thông báo qua ứng dụng di động cho người dùng về tình trạng của hệ thống chong đèn. Người dùng có thể điều khiển từ xa cho hệ thống hoạt động theo ý muốn của mình mà không cần phải tới tận vườn.
  + Ứng dụng được cập nhật dữ liệu thời gian thực thông qua Firebase để thông báo sớm nhất về tình trạng của hệ thống chong đèn để người dùng có hướng giải quyết ngay khi hệ thống xảy ra sự cố hoặc bị trộm.
  + Hệ thống có thể đặt trước thời gian gian kéo/tắt điện ngay tại nhà khi tới giờ kéo điện sẽ tự động bật điện và khi tới giờ tắt điện lại tự động tắt. Trong quá trình chong đèn mỗi khi có mưa sẽ tự động tắt, hết mưa lại kéo lên lại.
  + Hệ thống có Server liên tục lấy dữ liệu dự báo thời tiết từ API để xử lý và tính toán thời gian kéo/tắt điện sau đó cập nhật các móc thời gian lên Firebase. Ngoài ra Firebase còn là nơi điều khiển đóng tắt đèn khi đến các giờ đã cài đặt trên điện thoại.
  + Hai chế độ của hệ thống đều có thể được người dùng thay đổi trên ứng dụng di động. Nó sẽ rất hữu dụng khi người dùng muốn điều chỉnh hoặc dể để kiểm thử hệ thống.

2. **Kết quả đạt được** 
  + Hệ thống lấy dữ liệu từ các cảm biến, sau đó sẽ được xử lý qua bộ xử lý trung tâm và hiển thị trên APP điện thoại bằng một nền tảng trung gian là Firebase.<br>
![image](https://github.com/NinhLuong/appcontrol/assets/90811122/10ad03b8-4a2e-4767-9fa9-46a379b418c8)
  + Dữ liệu sẽ hiển thị lên ứng dụng trên điện thoại được thiết kế.
    ![image](https://github.com/NinhLuong/appcontrol/assets/90811122/9789cbc1-fcc9-4995-9589-0da343f44176)
	![image](https://github.com/NinhLuong/appcontrol/assets/90811122/03bc7a92-9093-42ed-8e55-83f6605bdc29)
  + Hệ thống có khả năng kiểm tra chập cháy của dây điện và báo trộm khi có sự can thiệp từ bên ngoài tắt điện trong quá trình chong điện để trộm dây điện bóng đèn. Khi điều đó xảy ra biến SOS trên Firebase sẽ đổi từ “false” thành “true” ngay lập tức điện thoại bật chuông thông báo để cách báo người dùng để đưa  ra hướng giải quyết.
  ![image](https://github.com/NinhLuong/appcontrol/assets/90811122/af116e33-a968-43a1-8840-ef2c025258d7)
  + Hệ thống sẽ có một server chạy liên tục để lấy dữ liệu dự báo thời tiết, tiến hành xử lý dữ liệu để đưa ra thời gian bật/tắt dự kiến một cách tối ưu nhất. Bên cạnh đó hệ thống server cũng theo dõi quá trình trong điện tính toán đủ giờ trong điện thì lập tức ngắt điện để không xảy ra hao phí điện.
	![image](https://github.com/NinhLuong/appcontrol/assets/90811122/d6ae3933-1e60-415e-bf8c-de308fc3aa33)
	![image](https://github.com/NinhLuong/appcontrol/assets/90811122/0c4232cb-68e9-4bca-bee9-99074dfabac3)





  

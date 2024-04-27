import socket
from keras.models import load_model
import numpy as np
import pandas as pd
import io
import time
np.set_printoptions(precision=4, suppress=True)



# 显示较大的数组时不截断
np.set_printoptions(threshold=10000)
model = load_model('E:\\Download\\my_wd_cnn_model.h5')
# 设置服务器地址和端口号
SERVER_ADDRESS = 'localhost'  # 若服务器在同一台机器上，使用'localhost'；若远程服务器，请填写其IP地址
SERVER_PORT = 60000
# 创建一个套接字
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 连接到服务器
client_socket.connect((SERVER_ADDRESS, SERVER_PORT))



while True:
     time.sleep(4)
     response = client_socket.recv(2048*200).decode()
     #print(response)  
     # 如果数据是CSV格式的，你可以使用io.StringIO来创建一个文件对象，然后使用pandas的read_csv函数来读取它  
     data_io = io.StringIO(response[1:])
     df = pd.read_csv(data_io, header=None)
     df=df.apply(pd.to_numeric, errors='coerce')
     df[2047]=0
     df=df.values.reshape(1, 2048, 1)

     pre=model.predict(df)
     predicted_classes = np.argmax(pre, axis=1)
     print(predicted_classes)
     client_socket.sendall((str(predicted_classes)+'\n').encode())

     
# 如果需要持续通信，可以将上述发送和接收放在循环中

# 关闭套接字
client_socket.close()
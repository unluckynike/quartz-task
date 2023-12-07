# coding：utf-8
'''
@Project ：stdaily 
@File    ：test_temp.py
@Author  ：hailin
@Date    ：2023/12/7 11:21 
@Info    : 
'''
from datetime import datetime
import requests
import random
import time
import pymysql
from bs4 import BeautifulSoup


def get_html(start_page):
    url = "https://mp.weixin.qq.com/cgi-bin/appmsg"
    params = {
        "action": "list_ex",
        "begin": start_page,
        "count": "5",
        "fakeid": "MzA4NDk3ODEwNQ==",
        "type": "9",
        "query": "",
        "token": "477996450",
        "lang": "zh_CN",
        "f": "json",
        "ajax": "1"
    }
    headers = {
        "accept": "*/*",
        "accept-encoding": "gzip, deflate, br",
        "accept-language": "zh-CN,zh;q=0.9,en;q=0.8",
        "cache-control": "no-cache",
        "cookie": "ptcz=a63f05ee3fcb0cdef2455cabc85a66fd383aa8db2770fbbb2144147c1d9c2c83; pgv_pvi=5372290048; RK=8m7IWecjGB; iip=0; tvfe_boss_uuid=0f45aec4338cdd0a; o_cookie=1239324047; pac_uid=1_1239324047; pgv_pvid=6876248496; ua_id=Xf0dBBoIaqNJYYdtAAAAAIDpPeYJI5pPb2OMFe7tzpQ=; wxuin=80421302860938; mm_lang=zh_CN; eas_sid=S1l6C8M0y522Q8G4r1T5Y9X7H0; rand_info=CAESICExqQobuLz7wbr0CrEo5y7J3VePzzYgWR7U3T7KIW0p; slave_bizuin=3874922077; data_bizuin=3874922077; bizuin=3874922077; data_ticket=G+ucXOdEUXNU91ObHJQomfj3OAKwOTK+k93yfaa4bHu0EuvGJFflNI1pIWs6QwP/; slave_sid=VkxBVDB3R21xV3dTeUU2Q0Q2MThBR0l3TnlJSFd6YkdoNkJtOFI1MVdBUWRfNUtsU0lqMndjblJUN01MampWZ2FXSHRvX3FYRUV5OVhzeDF5aTFYQXJlbGxlMW5PejFVRTU2S25oaHFOMlg3YkI3SVBySGRDOUxEYnNWamwwSGh5dkxGVTlIRE1kRWgzN0l2; slave_user=gh_fe6961cc328f; xid=53b32cec4b42211491dd6dbad0fe1463; _clck=3874922077|1|fhb|0; _clsk=1p0ro2c|1701870448709|2|1|mp.weixin.qq.com/weheat-agent/payload/record",
        "pragma": "no-cache",
        "referer": "https://mp.weixin.qq.com/cgi-bin/appmsg?t=media/appmsg_edit_v2&action=edit&isNew=1&type=77&createType=0&token=477996450&lang=zh_CN&timestamp=1701870446626",
        "sec-ch-ua": """Google Chrome";v="111", "Not(A:Brand";v="8", "Chromium";v="111""",
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-platform": """Windows""",
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "same-origin",
        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
        "x-requested-with": "XMLHttpRequest"
    }
    r = requests.get(url, params=params, headers=headers)
    if (r.status_code != 200):
        raise Exception("error!")
    htmls = r.json()
    return htmls


# 获取文章文本
def get_text_html(link):
    url = link
    text_content = ''
    header = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"}
    req = requests.get(url=url, headers=header)
    if (req.status_code != 200):
        raise Exception("error!")
    req.encoding = 'UTF-8'
    html = req.text
    bes = BeautifulSoup(html, "html.parser")
    texts = bes.find("body", id="activity-detail").find("div", id="js_article") \
        .find("div", id="js_base_container").find("div", id="page-content").find("div",
                                                                                 class_="rich_media_area_primary_inner") \
        .find("div", id="img-content").find("div", id="js_content").find_all("span")
    for content in texts:
        text_content += content.text
    print(1)
    print(text_content)
    return text_content


# 获取信息
def get_info(htmls, source):
    results = {}
    time_count = 0
    counter = 0
    jishuqi = 0
    editors = []
    editors.append(source)

    for info in htmls['app_msg_list']:
        source = source  # 数据源
        cover = info['cover']  # 封面链接
        title = info['title']  # 文章标题
        link = info['link']  # 文章链接
        abstract = info['digest']  # 文章简介
        try:
            text = get_text_html(link)  # 文章文本内容
        except Exception:
            text = 'null'
        tep_time = datetime.fromtimestamp(info['update_time'])  # 发布时间
        # datetime.datetime类型需转化为str类型
        update_time = tep_time.strftime("%Y-%m-%d %H:%M:%S")

        results["news_page_url"] = None
        results["news_url"] = link
        results["source"] = source
        results["title"] = title
        results["date"] = update_time
        results["content"] = text
        results["country"] = "中国"
        results["source_type"] = "1"
        results["photo_url"] = cover
        results["editors"] = editors
        results["abstract"] = abstract

        counter += 1
        time_count += 1
        if time_count % 5 == 0:
            sleep_time = 10 + random.random()
        else:
            sleep_time = random.randint(0, 5) + random.random()
        time.sleep(sleep_time)
        print("正在爬取第", counter, "个数据，休息", sleep_time, "秒")

        print("===========w_database()方法执行========")
        # w_database(results, table_name="news")

        results.clear()


# mysql
# 需要输入数据库的表名，json文件地址
def w_database(results, table_name):
    # 1.连接自己数据库
    conn = pymysql.connect(host="localhost", user="root", password="root",
                           database="wechat_crawler", port=3306, charset="utf8mb4")
    print("数据库连接成功！")
    # 2.打开已存的json文件，需要更换
    # temp = json.loads(results)
    # print(temp)
    temp = results
    # 3.生成cursor游标对象
    cursor = conn.cursor()
    # 4.建表,建过了把这第四步注释掉
    # sql_creat = f"""CREATE TABLE {table_name} (
    # `news_page_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻来源URL',
    # `news_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻原文URL',
    #   `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新闻来源',
    #   `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻标题',
    #   `date` datetime NULL DEFAULT NULL COMMENT '新闻日期',
    #   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻原文',
    #   `country` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新闻发布所在国家',
    #   `source_type` int(11) NULL DEFAULT NULL COMMENT '新闻来源类型',
    #   `photo_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻的封面图片URL',
    #   `editors` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新闻的编辑或作者，团体或个人',
    #    `abstract` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新闻原文摘要',
    #    `news_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
    #    PRIMARY KEY (`news_id`) USING BTREE
    #                                   )"""
    # cursor.execute(sql_creat)
    # print("建表成功！")

    # 5.sql循环插入赋值语句  %s占位符
    news_page_url = temp['news_page_url']
    news_url = temp['news_url']
    source = temp['source']
    title = temp['title']
    date = temp['date']
    content = temp['content']
    country = temp['country']
    source_type = temp['source_type']
    photo_url = temp['photo_url']
    editors = temp['editors']
    abstract = temp['abstract']
    # news_id = temp['news_id']

    value = [news_page_url, news_url, source, title, date, content, country,
             source_type, photo_url, editors, abstract]
    # sql语句
    sql_insert = f"INSERT INTO {table_name} (news_page_url,news_url,source,title,date,content,country,source_type,photo_url,editors,abstract) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
    cursor.execute(sql_insert, value)
    print("写入成功！")
    # 6.提交sql
    conn.commit()
    # 7.关闭连接
    cursor.close()
    conn.close()


if __name__ == "__main__":

    for start_page in range(0, 1, 5):
        htmls = get_html(start_page)
        get_info(htmls, "材料科学与工程")

package ro.georgemarinescu.myhealth.models

class DataSource {

        companion object{
            fun createDataSet(): ArrayList<CardPost>{
                val list = ArrayList<CardPost>()
                list.add(
                    CardPost(
                        "General",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/General.jpg"

                    )
                )
                list.add(
                    CardPost(
                        "ORL",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/ORL.jpg"

                    )
                )
                list.add(
                    CardPost(
                        "Cardiologie",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/cardiologie.png"

                    )
                )

                list.add(
                    CardPost(
                        "Dermatologie",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/dermatologie.jpg"

                    )
                )
                list.add(
                    CardPost(
                        "Neurologie",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/neurologie.png"

                    )
                )
                list.add(
                    CardPost(
                        "Psihologie",
                        "https://raw.githubusercontent.com/JackieJester/MyHealth_Images/master/psihologie.jpg"

                    )
                )
                return list
            }


        }
}
# Deklaer

Deklaer is an Ontology-Driven framework to facilitate the development and deployment of IoT applications. We use
ContextNet, witch is a middleware for IoT applications, and a declarative language implemented using an OWL ontology that
provides a way for the developer to describe which sensors to use. Moreover, depending on the data acquired by each sensor, which
is the actuation that the application is to effect as response to the sensor’s data. By using Deklear we developed ICare, which is an application
created using Deklaer, to monitor physiological data of elderly people with special needs.

Link to the article: https://ieeexplore.ieee.org/document/8538718

## Como executar:

* 1) Primeiro deve ser instalado o SDDL na máquina. O procedimento para instalação dele esta no wiki do LAC nesse link: http://wiki.lac.inf.puc-rio.br/doku.php?id=download
* É preciso levantar um servidor SDDL com essa linha de comando que esta no final do tutorial do link acima.
```
java -jar contextnet-2.7.jar 127.0.0.1 5500 OpenSplice
```

* 2) Deve ter instalado o Timescale DB. O Deklaer usa esse banco de dados para guardar os dados coletados de sensores. 
* Timescale DB: https://www.timescale.com

Na classe ContextDbManager.java deve ser alterado o usuário e senha do banco de dados.

* 3)	Na classe ObsActDbManager.java deve ser alterado a função 
```
private voit registerPatients()
{
    registerNewPatient("UUID do MobileHub", "Path to patient ontologies folder");
}
```

* Como ainda não tinha feito uma interface gráfica para que o usuário possa setar os pacientes. Os pacientes são as entidades que devem ser monitoradas. Nesta função deve ser colocado o UUID do MHub do paciente e o path da ontologia ObsAct do paciente
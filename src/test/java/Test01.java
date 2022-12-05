import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * Author: luwei
 * Date: 2022/12/3 21:41
 */
@Slf4j
public class Test01 {

    ProcessEngineConfiguration configuration = null;

    /**
     * @Before资源初始化
     */
    @Before
    public void before(){
        // 获取ProcessEngineConfiguration对象
        configuration=new StandaloneInMemProcessEngineConfiguration();
        //配置数据库连接
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("yueJi@2018");
        configuration.setJdbcUrl("jdbc:mysql://192.168.43.66:3306/flowable_learn?serverTimezone=Asia/Shanghai");
        //库中表结构不存在就新建
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        log.debug("数据库初始化成功！");
    }

    /**
     * 获取流程引擎对象，单元测试
     */
    @Test
    public void testProcessEngine(){
        // 通过ProcessEngineConfiguration构建我们需要的processEngine对象
        ProcessEngine processEngine = configuration.buildProcessEngine();
        log.debug("processEngine " + processEngine);
    }

    /**
     * 部署流程
     */
    @Test
    public void testDeploy() {
        //获取流程引擎对象
        ProcessEngine processEngine = configuration.buildProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //完成流程引擎部署操作
        Deployment depoly = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml")
                .name("请求流程")
                .deploy();
        log.debug("deploy engine Id= " + depoly.getId() + " name= " + depoly.getName());
    }

    /**
     * 查询流程定义
     */
    @Test
    public void queryDeploy() {
        try {
            //获取流程引擎对象enginel
            ProcessEngine processEngine = configuration.buildProcessEngine();
            //获取RepositoryService资源管理服务
            RepositoryService repositoryService = processEngine.getRepositoryService();
            //获取查询流程定义对象
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId("1")
                    .singleResult();
            log.debug("deploymentId " + processDefinition.getDeploymentId() + " deploymentName " + processDefinition.getName());
            log.debug("Id " + processDefinition.getId() + " description " + processDefinition.getDescription());
        } catch (Exception e) {
            log.error("查询流程定义失败", e);
        }
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteDeploy() {
        try{
            //获取流程引擎engine
            ProcessEngine processEngine = configuration.buildProcessEngine();
            //获取RepositoreService资源管理服务
            RepositoryService repositoryService = processEngine.getRepositoryService();
            //第一个参数流程id，如果部署流程启动了就不允许删除
            //第二个参数是级联删除，若为true，若流程启动了，相关任务会一并删除
            repositoryService.deleteDeployment("2501", false);
            log.debug("删除流程定义成功");
        }catch (Exception e){
            log.error("删除流程定义失败",e);
        }
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startInstance() {
        //获取流程引擎对象engine
        ProcessEngine processEngine = configuration.buildProcessEngine();
        //通过RuntimeService启动流程引擎
        RuntimeService runtimeService = configuration.getRuntimeService();

        //构建流程引擎变量
        HashMap variables = new HashMap(16);
        variables.put("employee", "张三");
        variables.put("nrOfHolidays", 3);
        variables.put("description", "工作累了，出去玩玩");

        //启动流程引擎
        ProcessInstance holidayRequest = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        log.debug("deffenitionId " + holidayRequest.getProcessDefinitionId() + " activityId " + holidayRequest.getActivityId() + " id " + holidayRequest.getId());
    }


    /**
     * @After释放资源
     */
    @After
    public void close(){
        log.debug("释放资源");
    }

}

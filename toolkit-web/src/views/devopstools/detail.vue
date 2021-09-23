<template>
  <div class="app-container" style="font-size: 15px;">
    <div>
      <span>{{ $t('devopstools.taskName') }} :</span>
      <el-input v-model="devopsName" size="mini" disabled style="width:20%;margin-left: 10px;" />
    </div>
    <div style="margin-top: 10px;">
      <span style="vertical-align:top;">{{ $t('devopstools.featuresDesc') }} :</span>
      <el-input v-model="deptdesc" type="textarea" size="mini" :rows="3" disabled style="width:40%;margin-left: 10px;" />
    </div>
    <div v-for="(item,index) in step" :key="index" style="margin-top: 20px;">
      <el-card style="width: 90%;">
        <div>
          <div class="div-a">
            {{ $t('devopstools.step') }}{{ item.id }}:
            {{ item.name }}
          </div>
          <div class="div-b">
            <el-button v-if="execHis[item.id].status === '0'" round size="mini" disabled style="color:blue;">{{ statusText(item.id) }}</el-button>
            <el-button v-if="execHis[item.id].status === '1'" round size="mini" disabled style="color:green;">{{ statusText(item.id) }}</el-button>
            <el-button v-if="execHis[item.id].status === '2'" round size="mini" disabled style="color:red;">{{ statusText(item.id) }}</el-button>
          </div>
        </div>
        <div style="margin-top: 40px;margin-left: 40px;">
          <div class="div-a">
            {{ $t('devopstools.description') }}:
            {{ item.desc }}
          </div>
          <div class="div-b">
            <el-button type="primary" :disabled="!(item.type === 'QUERY' || execHis[item.id].status === '0')" size="mini" @click="execTask(item.id, execHis[item.id].execParam)">{{ $t('devopstools.execution') }}</el-button>
            <el-button v-if="item.type==='UPDATE'" :disabled="execHis[item.id].status != '1'" size="mini" @click="execRollbackTask(item.id)">{{ $t('devopstools.revoke') }}</el-button>
          </div>
        </div>
        <div style="margin-top: 80px;margin-left: 40px;">
          {{ $t('devopstools.condition') }}:
          <span v-if="item.paramsOfWhere != null">
            <span v-for="(ment,num) in item.paramsOfWhere.param" :key="num">
              {{ ment.value }}: <el-input v-model="execHis[item.id].execParam[ment.key]" :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
        <div v-if="item.type==='QUERY'" :gutter="20" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.result') }}:
          <span v-if="execHis[item.id].execData!=null">
            <span v-for="(value,key,num) in execHis[item.id].execData" :key="num" class="usernamage">
              {{ key }}
              <div class="usernamage-wrap" style="margin-left: 40px;margin-top: 10px;">
                <div class="tabledata">
                  <el-table
                    ref="multipleTable"
                    :data="execHis[item.id].execData[key]"
                    stripe
                    element-loading-text="Loading"
                    style="width: 100%"
                    max-height="300"
                    :header-cell-style="{background:'#E5E9EF',color:'#606266'}"
                    border
                  >
                    <el-table-column v-if="execHis[item.id].execData[key].length>0" type="index" align="center" :label="$t('devopstools.num')" width="60" />
                    <el-table-column v-for="(val,bond,number) in execHis[item.id].execData[key][0]" :key="number" align="center" :label="bond" min-width="300">
                      <template v-slot="scope">
                        {{ scope.row[bond] }}
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </span>
          </span>
        </div>
        <div v-if="item.type==='UPDATE'" :gutter="20" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.updated') }}:
          <span v-if="item.paramsOfSet != null" :span="20">
            <span v-for="(ment,num) in item.paramsOfSet.param" :key="num">
              {{ ment.value }}: <el-input v-model="execHis[item.id].execParam[ment.key]" :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
      </el-card>
    </div>
    <el-button type="primary" style="margin-left: 45%;margin-top: 20px;" @click="btnReturn()">{{ $t('common.btnReturn') }}</el-button>
  </div>
</template>
<script>
import { getTaskById, execStep, execRollback } from '@/api/devops.js'
import { checkMobile, checkTel, checkMail, checkDate, checkSocialCreditCode, checkFax, checkZipCode } from '@/utils/common.js'
export default {
  data() {
    return {
      devopsName: '',
      deptdesc: '',
      step: [],
      execHis: {},
      execForm: {
        id: 0,
        stepId: 0,
        params: {}
      },
      rules: {},
      paramName: {}
    }
  },
  mounted() {
    this.getTool()
  },
  methods: {
    statusText(data) {
      if (this.execHis[data].status === '0') {
        return this.$t('devopstools.notExecuted')
      }
      if (this.execHis[data].status === '1') {
        return this.$t('devopstools.executed')
      }
      if (this.execHis[data].status === '2') {
        return this.$t('devopstools.revoked')
      }
    },
    getTool() {
      getTaskById(this.$route.params.taskId).then(res => {
        if (res.data && res.code === 0) {
          this.devopsName = res.data.name
          this.deptdesc = res.data.desc
          this.step = res.data.devops.steps.step
          this.execHis = res.data.execHis
          for (var i = 0; i < this.step.length; i++) {
            if (this.execHis[this.step[i].id].execParam == null) {
              // this.execHis[this.step[i].id].execParam = {}
              // 设置双向绑定
              this.$set(this.execHis[this.step[i].id], 'execParam', {})
              if (this.step[i].paramsOfWhere != null) {
                for (var j = 0; j < this.step[i].paramsOfWhere.param.length; j++) {
                  // 记录check规则
                  this.rules[this.step[i].paramsOfWhere.param[j].key] = this.step[i].paramsOfWhere.param[j].rules
                  this.paramName[this.step[i].paramsOfWhere.param[j].key] = this.step[i].paramsOfWhere.param[j].value
                  // this.execHis[this.step[i].id].execParam[this.step[i].paramsOfWhere.param[j].key] = "bb"
                  // 设置双向绑定
                  this.$set(this.execHis[this.step[i].id].execParam, this.step[i].paramsOfWhere.param[j].key, '')
                }
              }
              if (this.step[i].paramsOfSet != null) {
                for (var k = 0; k < this.step[i].paramsOfSet.param.length; k++) {
                  // 记录check规则
                  this.rules[this.step[i].paramsOfSet.param[k].key] = this.step[i].paramsOfSet.param[k].rules
                  this.paramName[this.step[i].paramsOfSet.param[k].key] = this.step[i].paramsOfSet.param[k].value
                  // this.execHis[this.step[i].id].execParam[this.step[i].paramsOfSet.param[k].key] = "aa"
                  // 设置双向绑定
                  this.$set(this.execHis[this.step[i].id].execParam, this.step[i].paramsOfSet.param[k].key, '')
                }
              }
            } else {
              if (this.step[i].paramsOfWhere != null) {
                for (var m = 0; m < this.step[i].paramsOfWhere.param.length; m++) {
                  // 记录check规则
                  this.rules[this.step[i].paramsOfWhere.param[m].key] = this.step[i].paramsOfWhere.param[m].rules
                  this.paramName[this.step[i].paramsOfWhere.param[m].key] = this.step[i].paramsOfWhere.param[m].value
                }
              }
              if (this.step[i].paramsOfSet != null) {
                for (var n = 0; n < this.step[i].paramsOfSet.param.length; n++) {
                  // 记录check规则
                  this.rules[this.step[i].paramsOfSet.param[n].key] = this.step[i].paramsOfSet.param[n].rules
                  this.paramName[this.step[i].paramsOfSet.param[n].key] = this.step[i].paramsOfSet.param[n].value
                }
              }
            }
          }
          // console.log(this.rules)
          // alert(this.execHis)
          // console.log(this.execHis[1].execData.查询结果2)
        }
      })
    },
    execTask(stepId, params) {
      for (const key in params) {
        params[key] = params[key].trim()
        if (params[key] === '') {
          this.$message({
            type: 'warning',
            message: this.$t('devopstools.paramsNull'),
            duration: 1000
          })
          return
        }
        switch (this.rules[key]) {
          case 'mobile':
            if (!checkMobile(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'tel':
            if (!checkTel(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'mail':
            if (!checkMail(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'date':
            if (!checkDate(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'socialCreditCode':
            if (!checkSocialCreditCode(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'fax':
            if (!checkFax(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'zipCode':
            if (!checkZipCode(params[key])) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
            break
          case 'telNum':
            if (!(checkMobile(params[key]) || checkTel(params[key]))) {
              this.$message({
                type: 'warning',
                message: this.$t('devopstools.wrongFormat').replace('{0}', this.paramName[key]),
                duration: 1000
              })
              return
            }
        }
      }
      // 是否执行？
      this.$confirm(this.$t('devopstools.executionConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.execForm.id = this.$route.params.taskId
        this.execForm.stepId = stepId
        this.execForm.params = params
        execStep(this.execForm).then(res => {
          if (res.code === 0) {
            this.getTool()
            this.$message({
              type: 'success',
              message: this.$t('devopstools.executionSuccess'),
              duration: 1000
            })
          }
        })
      })
    },
    execRollbackTask(stepId) {
      // 是否撤销？
      this.$confirm(this.$t('devopstools.revokeConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.execForm.id = this.$route.params.taskId
        this.execForm.stepId = stepId
        // this.execForm.params = params
        execRollback(this.execForm).then(res => {
          if (res.code === 0) {
            this.getTool()
            this.$message({
              type: 'success',
              message: this.$t('devopstools.revokeSuccess'),
              duration: 1000
            })
          }
        })
      })
    },
    btnReturn() {
      this.$router.push({ path: '/devopstools/task' })
    }
  }
}
</script>

<style scoped>
.usernamage{text-align: left;width: 98%;height: 100%;margin: 0 auto;overflow: hidden;font-size: 15px;}
.usernamage-wrap{height: 100%;background: #ffffff;border-radius: 6px;overflow: hidden;}
.usernamage /deep/ .el-dialog__body {padding: 0 20px;}
.usernamage /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-form-item{margin-bottom: 0;}
.tablepage{text-align: right;}
.padingb10{padding-bottom: 18px;}
.usernamage  /deep/ .el-form-item__label{ text-align: right;text-align-last: inherit;}
.tabledata /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-table td, .el-table th{padding: 3px 0;}
.block{display: flex;}
.usernamage{padding: 10px;}
.div-a{ float:left;width:88%;}
.div-b{ float:left;width:12%;}
</style>

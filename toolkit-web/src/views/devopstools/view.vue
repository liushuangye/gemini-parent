<template>
  <div class="app-container" style="font-size: 15px;">
    <div>
      <span>{{ $t('devopstools.toolName') }} :</span>
      <el-input v-model="devopsName" size="mini" disabled style="width:20%;margin-left: 10px;" />
    </div>
    <div style="margin-top: 10px;">
      <span style="vertical-align:top;">{{ $t('devopstools.featuresDesc') }} :</span>
      <el-input v-model="deptdesc" type="textarea" size="mini" :rows="3" disabled style="width:40%;margin-left: 10px;" />
    </div>
    <div v-for="(item,index) in step" :key="index" style="margin-top: 20px;">
      <el-card style="width: 90%;">
        <div>
          {{ $t('devopstools.step') }}{{ item.id }}:
          {{ item.name }}
        </div>
        <div style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.description') }}:
          {{ item.desc }}
        </div>
        <div style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.condition') }}:
          <span v-if="item.paramsOfWhere != null">
            <span v-for="(ment,num) in item.paramsOfWhere.param" :key="num">
              {{ ment.value }}: <el-input disabled :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
        <div v-if="item.type==='QUERY'" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.result') }}:
        </div>
        <div v-if="item.type==='UPDATE'" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.updated') }}:
          <span v-if="item.paramsOfSet != null">
            <span v-for="(ment,num) in item.paramsOfSet.param" :key="num">
              {{ ment.value }}: <el-input disabled :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
      </el-card>
    </div>
    <el-button type="primary" style="margin-left: 45%;margin-top: 10px;" @click="btnReturn()">{{ $t('common.btnReturn') }}</el-button>
  </div>
</template>
<script>
import { getById } from '@/api/devops.js'
export default {
  data() {
    return {
      devopsName: '',
      deptdesc: '',
      step: []
    }
  },
  mounted() {
    this.getTool()
  },
  methods: {
    getTool() {
      getById(this.$route.params.devopsId).then(res => {
        if (res.data && res.code === 0) {
          this.devopsName = res.data.devopsName
          this.deptdesc = res.data.deptdesc
          this.step = res.data.devops.steps.step
        }
      })
    },
    btnReturn() {
      this.$router.push({ path: '/devopstools/index' })
    }
  }
}
</script>

<style scoped>
.div-a{ float:left;width:90%;}
.div-b{ float:left;width:10%;}
</style>

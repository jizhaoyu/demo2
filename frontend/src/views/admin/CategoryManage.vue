<template>
  <div class="category-manage-page">
    <n-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <n-icon size="28" color="#2080f0"><GridOutline /></n-icon>
            <div>
              <h2>分类管理</h2>
              <p>管理书籍分类，支持排序</p>
            </div>
          </div>
          <n-button type="primary" @click="showAddModal">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            添加分类
          </n-button>
        </div>
      </template>
      
      <!-- 统计信息 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">{{ categories.length }}</span>
          <span class="stat-label">分类总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ totalBooks }}</span>
          <span class="stat-label">书籍总数</span>
        </div>
      </div>
      
      <n-spin :show="loading">
        <div v-if="categories.length > 0" class="category-list">
          <div v-for="(category, index) in categories" :key="category.id" class="category-item">
            <div class="category-index">{{ index + 1 }}</div>
            <div class="category-icon" :style="{ background: getCategoryColor(index) }">
              <n-icon size="20" color="#fff"><component :is="getCategoryIcon(index)" /></n-icon>
            </div>
            <div class="category-info">
              <span class="category-name">{{ category.name }}</span>
              <div class="category-meta">
                <span class="category-sort">排序: {{ category.sort }}</span>
                <span class="category-count">
                  <n-icon size="14"><BookOutline /></n-icon>
                  {{ category.bookCount || 0 }} 本书籍
                </span>
              </div>
            </div>
            <div class="category-actions">
              <n-button size="small" quaternary @click="showEditModal(category)">
                <template #icon><n-icon><CreateOutline /></n-icon></template>
                编辑
              </n-button>
              <n-popconfirm @positive-click="handleDelete(category)">
                <template #trigger>
                  <n-button size="small" quaternary type="error" :disabled="(category.bookCount || 0) > 0">
                    <template #icon><n-icon><TrashOutline /></n-icon></template>
                    删除
                  </n-button>
                </template>
                确定要删除分类"{{ category.name }}"吗？
              </n-popconfirm>
            </div>
          </div>
        </div>
        
        <n-empty v-else description="暂无分类，点击上方按钮添加" />
      </n-spin>
    </n-card>
    
    <n-modal v-model:show="modalVisible" preset="card" style="width: 420px"
             :title="editingCategory ? '编辑分类' : '添加分类'">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="分类名称" path="name">
          <n-input v-model:value="form.name" placeholder="请输入分类名称" maxlength="20" show-count />
        </n-form-item>
        <n-form-item label="排序值" path="sort">
          <n-input-number v-model:value="form.sort" :min="0" :max="999" placeholder="数字越小越靠前" style="width: 100%" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="modalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage, FormInst } from 'naive-ui'
import { GridOutline, AddOutline, CreateOutline, TrashOutline, BookOutline,
         LibraryOutline, SchoolOutline, FlaskOutline, ColorPaletteOutline, 
         HomeOutline, EllipsisHorizontalOutline } from '@vicons/ionicons5'
import { categoryApi, type CategoryVO } from '@/service/category'

const message = useMessage()
const loading = ref(false)
const categories = ref<CategoryVO[]>([])
const modalVisible = ref(false)
const submitting = ref(false)
const editingCategory = ref<CategoryVO | null>(null)
const formRef = ref<FormInst | null>(null)
const form = reactive({ name: '', sort: 0 })
const rules = { 
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 20, message: '分类名称不能超过20个字符', trigger: 'blur' }
  ] 
}

// 计算书籍总数
const totalBooks = computed(() => {
  return categories.value.reduce((sum, cat) => sum + (cat.bookCount || 0), 0)
})

// 分类图标
const categoryIcons = [LibraryOutline, SchoolOutline, FlaskOutline, ColorPaletteOutline, HomeOutline, EllipsisHorizontalOutline]
function getCategoryIcon(index: number) {
  return categoryIcons[index % categoryIcons.length]
}

// 分类颜色
const categoryColors = ['#18a058', '#2080f0', '#f0a020', '#d03050', '#722ed1', '#909399']
function getCategoryColor(index: number) {
  return categoryColors[index % categoryColors.length]
}

async function loadCategories() {
  loading.value = true
  try {
    const res = await categoryApi.listWithBookCount()
    categories.value = res.data || []
  } catch {} finally { loading.value = false }
}

function showAddModal() {
  editingCategory.value = null
  form.name = ''
  form.sort = categories.value.length
  modalVisible.value = true
}

function showEditModal(category: CategoryVO) {
  editingCategory.value = category
  form.name = category.name
  form.sort = category.sort
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitting.value = true
    if (editingCategory.value) {
      await categoryApi.update(editingCategory.value.id, form)
      message.success('更新成功')
    } else {
      await categoryApi.create(form)
      message.success('添加成功')
    }
    modalVisible.value = false
    loadCategories()
  } catch {} finally { submitting.value = false }
}

async function handleDelete(category: CategoryVO) {
  if ((category.bookCount || 0) > 0) {
    message.warning('该分类下还有书籍，无法删除')
    return
  }
  try {
    await categoryApi.delete(category.id)
    message.success('删除成功')
    loadCategories()
  } catch {}
}

onMounted(loadCategories)
</script>

<style scoped>
.category-manage-page { padding: 24px; }

.main-card { border-radius: 16px; max-width: 800px; }

.card-header { display: flex; justify-content: space-between; align-items: center; }

.header-left { display: flex; align-items: center; gap: 16px; }
.header-left h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.header-left p { margin: 0; color: #666; font-size: 14px; }

.stats-row {
  display: flex;
  gap: 24px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-radius: 12px;
  margin-bottom: 20px;
}

.stat-item { display: flex; flex-direction: column; }
.stat-value { font-size: 24px; font-weight: 700; color: #2080f0; }
.stat-label { font-size: 13px; color: #666; }

.category-list { display: flex; flex-direction: column; gap: 8px; }

.category-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.2s;
}

.category-item:hover { background: #f0f0f0; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }

.category-index {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: #e8e8e8;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-info { flex: 1; }
.category-name { font-size: 16px; font-weight: 500; color: #1a1a1a; display: block; margin-bottom: 4px; }

.category-meta { display: flex; gap: 16px; }
.category-sort { font-size: 13px; color: #999; }
.category-count { 
  font-size: 13px; 
  color: #18a058; 
  display: flex; 
  align-items: center; 
  gap: 4px; 
}

.category-actions { display: flex; gap: 4px; }
</style>
